package com.fortytwo.demeter.fotos.service;

import com.fortytwo.demeter.common.cloudtasks.CloudTasksService;
import com.fortytwo.demeter.common.cloudtasks.ProcessingTaskRequest;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.common.tenant.TenantContext;
import com.fortytwo.demeter.fotos.dto.ClassificationDTO;
import com.fortytwo.demeter.fotos.dto.CreateImageRequest;
import com.fortytwo.demeter.fotos.dto.DetectionDTO;
import com.fortytwo.demeter.fotos.dto.ImageDTO;
import com.fortytwo.demeter.fotos.model.Image;
import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.repository.ClassificationRepository;
import com.fortytwo.demeter.fotos.repository.DetectionRepository;
import com.fortytwo.demeter.fotos.repository.ImageRepository;
import com.fortytwo.demeter.fotos.repository.PhotoProcessingSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ImageService {

    private static final Logger log = Logger.getLogger(ImageService.class);

    @Inject
    ImageRepository imageRepository;

    @Inject
    PhotoProcessingSessionRepository sessionRepository;

    @Inject
    DetectionRepository detectionRepository;

    @Inject
    ClassificationRepository classificationRepository;

    @Inject
    CloudTasksService cloudTasksService;

    @Inject
    TenantContext tenantContext;

    @Transactional
    public ImageDTO addImage(UUID sessionId, CreateImageRequest request) {
        return addImage(sessionId, request, "DETECTION");
    }

    @Transactional
    public ImageDTO addImage(UUID sessionId, CreateImageRequest request, String pipeline) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));

        Image image = new Image();
        image.setSession(session);
        image.setStorageUrl(request.storageUrl());
        image.setThumbnailUrl(request.thumbnailUrl());
        image.setOriginalFilename(request.originalFilename());
        image.setFileSize(request.fileSize());
        image.setMimeType(request.mimeType());

        imageRepository.persist(image);
        session.setTotalImages(session.getTotalImages() + 1);

        log.infof("Added image %s to session %s", image.getId(), sessionId);

        // Dispatch ML processing task
        dispatchProcessingTask(session, image, pipeline);

        return ImageDTO.from(image);
    }

    /**
     * Dispatch an ML processing task via Cloud Tasks.
     *
     * <p>Creates a Cloud Task that will invoke the ML Worker to process
     * the image. The task includes tenant isolation via tenant_id.
     */
    private void dispatchProcessingTask(PhotoProcessingSession session, Image image, String pipeline) {
        String tenantId = tenantContext.getCurrentTenantId();

        ProcessingTaskRequest taskRequest = ProcessingTaskRequest.of(
                tenantId,
                session.getId(),
                image.getId(),
                image.getStorageUrl(),
                pipeline
        );

        String taskName = cloudTasksService.createProcessingTask(taskRequest);

        if (taskName != null) {
            log.infof("Dispatched ML processing task: %s for image %s", taskName, image.getId());
        }
    }

    public List<ImageDTO> findBySession(UUID sessionId) {
        sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        return imageRepository.findBySessionId(sessionId)
                .stream().map(ImageDTO::from).toList();
    }

    public ImageDTO findById(UUID id) {
        Image image = imageRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Image", id));
        return ImageDTO.from(image);
    }

    public List<DetectionDTO> findDetectionsByImageId(UUID imageId) {
        imageRepository.findByIdOptional(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image", imageId));
        return detectionRepository.findByImageId(imageId)
                .stream().map(DetectionDTO::from).toList();
    }

    public List<ClassificationDTO> findClassificationsByImageId(UUID imageId) {
        imageRepository.findByIdOptional(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image", imageId));
        return classificationRepository.findByImageId(imageId)
                .stream().map(ClassificationDTO::from).toList();
    }
}
