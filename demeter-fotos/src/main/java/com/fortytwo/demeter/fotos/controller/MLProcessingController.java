package com.fortytwo.demeter.fotos.controller;

import com.fortytwo.demeter.common.cloudtasks.CloudTasksService;
import com.fortytwo.demeter.common.cloudtasks.ProcessingTaskRequest;
import com.fortytwo.demeter.common.tenant.TenantContext;
import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest;
import com.fortytwo.demeter.fotos.dto.SessionStatusDTO;
import com.fortytwo.demeter.fotos.model.Image;
import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.model.ProcessingStatus;
import com.fortytwo.demeter.fotos.repository.ImageRepository;
import com.fortytwo.demeter.fotos.repository.PhotoProcessingSessionRepository;
import com.fortytwo.demeter.fotos.service.ImageService;
import com.fortytwo.demeter.fotos.service.ProcessingResultService;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Production ML processing controller using Cloud Tasks.
 *
 * <p>Flow:
 * 1. Frontend uploads images
 * 2. Backend creates session + image records
 * 3. Backend enqueues tasks in Cloud Tasks
 * 4. Cloud Tasks calls ML Worker asynchronously
 * 5. ML Worker calls callback endpoint when done
 * 6. Frontend polls for status or receives webhook
 */
@Path("/api/v1/ml")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "ML Processing", description = "Production ML processing endpoints using Cloud Tasks")
@UnlessBuildProfile("dev")
public class MLProcessingController {

    private static final Logger log = Logger.getLogger(MLProcessingController.class);

    @Inject
    CloudTasksService cloudTasksService;

    @Inject
    PhotoProcessingSessionRepository sessionRepository;

    @Inject
    ImageRepository imageRepository;

    @Inject
    ImageService imageService;

    @Inject
    ProcessingResultService processingResultService;

    @Inject
    TenantContext tenantContext;

    @ConfigProperty(name = "demeter.backend.callback-url", defaultValue = "")
    String callbackBaseUrl;

    /**
     * Request for processing images via Cloud Tasks.
     */
    public record ProcessImagesRequest(
            List<ImageUpload> images,
            String pipeline
    ) {}

    public record ImageUpload(
            String filename,
            String contentType,
            String imageBase64
    ) {}

    @POST
    @Path("/process")
    @Operation(
            summary = "Upload and enqueue images for ML processing",
            description = "Uploads images and enqueues them for async processing via Cloud Tasks. " +
                    "Returns immediately with session ID. Poll /sessions/{id}/status for progress."
    )
    @Transactional
    public Response processImages(ProcessImagesRequest request) {
        if (request.images() == null || request.images().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "At least one image is required"))
                    .build();
        }

        String pipeline = request.pipeline() != null ? request.pipeline() : "SEGMENT_DETECT";
        String tenantId = tenantContext.getCurrentTenantId();

        try {
            // Create session
            PhotoProcessingSession session = new PhotoProcessingSession();
            session.setStatus(ProcessingStatus.PENDING);
            session.setTotalImages(request.images().size());
            session.setProcessedImages(0);
            sessionRepository.persist(session);

            log.infof("Created session: %s with %d images", session.getId(), request.images().size());

            // Process each image
            List<String> taskNames = new ArrayList<>();
            String callbackUrl = callbackBaseUrl.isEmpty() ? null : callbackBaseUrl + "/api/v1/ml/callback";

            for (ImageUpload upload : request.images()) {
                // Decode image
                byte[] imageData = decodeBase64Image(upload.imageBase64());
                String filename = upload.filename() != null ? upload.filename() : "image.jpg";
                String contentType = upload.contentType() != null ? upload.contentType() : "image/jpeg";

                // Upload to storage (returns gs:// URL)
                String storagePath = imageService.uploadAndStoreImage(
                        session.getId(), imageData, filename, contentType
                );

                // Create image record
                Image image = new Image();
                image.setSession(session);
                image.setStorageUrl(storagePath);
                image.setOriginalFilename(filename);
                image.setFileSize((long) imageData.length);
                image.setMimeType(contentType);
                imageRepository.persist(image);

                // Enqueue for ML processing
                ProcessingTaskRequest taskRequest = ProcessingTaskRequest.of(
                        tenantId,
                        session.getId(),
                        image.getId(),
                        storagePath,
                        pipeline,
                        callbackUrl
                );

                String taskName = cloudTasksService.createProcessingTask(taskRequest);
                if (taskName != null) {
                    taskNames.add(taskName);
                }
            }

            // Update session status
            session.setStatus(ProcessingStatus.PROCESSING);

            log.infof("Enqueued %d images for session %s", taskNames.size(), session.getId());

            return Response.accepted(Map.of(
                    "sessionId", session.getId(),
                    "imagesEnqueued", taskNames.size(),
                    "status", "PROCESSING",
                    "statusUrl", "/api/v1/photo-sessions/" + session.getId() + "/status"
            )).build();

        } catch (Exception e) {
            log.error("Failed to process images", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to process images: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Callback endpoint for ML Worker to report results.
     *
     * <p>Called by ML Worker when processing is complete.
     * This endpoint should be protected and only accessible from Cloud Tasks.
     */
    @POST
    @Path("/callback")
    @Operation(
            summary = "ML Worker callback",
            description = "Receives processing results from ML Worker. Called by Cloud Tasks."
    )
    @Transactional
    public Response processCallback(MLWorkerCallbackRequest request) {
        log.infof("Received ML callback: sessionId=%s, imageId=%s, success=%s",
                request.sessionId(), request.imageId(), request.success());

        try {
            if (request.success()) {
                // Convert and persist results
                ProcessingResultRequest resultRequest = new ProcessingResultRequest(
                        UUID.fromString(request.sessionId()),
                        UUID.fromString(request.imageId()),
                        convertDetections(request.detections()),
                        null, // classifications
                        null, // estimations
                        new ProcessingResultRequest.ProcessingMetadata(
                                request.pipeline(),
                                request.durationMs(),
                                null,
                                "cloud-tasks"
                        )
                );

                SessionStatusDTO status = processingResultService.processResults(resultRequest);

                return Response.ok(Map.of(
                        "success", true,
                        "sessionStatus", status
                )).build();
            } else {
                // Mark as failed
                log.errorf("ML processing failed: sessionId=%s, imageId=%s, error=%s",
                        request.sessionId(), request.imageId(), request.error());

                return Response.ok(Map.of(
                        "success", false,
                        "error", request.error()
                )).build();
            }
        } catch (Exception e) {
            log.error("Failed to process callback", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * Callback request from ML Worker.
     */
    public record MLWorkerCallbackRequest(
            String sessionId,
            String imageId,
            boolean success,
            String pipeline,
            long durationMs,
            List<DetectionItem> detections,
            String error
    ) {}

    public record DetectionItem(
            String className,
            double confidence,
            double x1, double y1, double x2, double y2
    ) {}

    private List<ProcessingResultRequest.DetectionResultItem> convertDetections(
            List<DetectionItem> detections
    ) {
        if (detections == null) return List.of();

        return detections.stream()
                .map(d -> new ProcessingResultRequest.DetectionResultItem(
                        d.className(),
                        d.confidence(),
                        new ProcessingResultRequest.BoundingBox(d.x1(), d.y1(), d.x2(), d.y2())
                ))
                .toList();
    }

    private byte[] decodeBase64Image(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) {
            throw new IllegalArgumentException("Image data is required");
        }
        // Remove data URL prefix if present
        String data = base64Data;
        if (data.contains(",")) {
            data = data.substring(data.indexOf(",") + 1);
        }
        return java.util.Base64.getDecoder().decode(data);
    }
}
