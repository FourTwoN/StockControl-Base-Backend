package com.fortytwo.demeter.fotos.service;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest;
import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest.ClassificationResultItem;
import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest.DetectionResultItem;
import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest.EstimationResultItem;
import com.fortytwo.demeter.fotos.dto.SessionStatusDTO;
import com.fortytwo.demeter.fotos.model.Classification;
import com.fortytwo.demeter.fotos.model.Detection;
import com.fortytwo.demeter.fotos.model.Estimation;
import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.model.ProcessingStatus;
import com.fortytwo.demeter.fotos.model.Image;
import com.fortytwo.demeter.fotos.repository.ClassificationRepository;
import com.fortytwo.demeter.fotos.repository.DetectionRepository;
import com.fortytwo.demeter.fotos.repository.EstimationRepository;
import com.fortytwo.demeter.fotos.repository.ImageRepository;
import com.fortytwo.demeter.fotos.repository.PhotoProcessingSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for processing ML Worker callback results.
 *
 * <p>Receives processing results from the ML Worker and persists
 * detections, classifications, and estimations to the database.
 */
@ApplicationScoped
public class ProcessingResultService {

    private static final Logger log = Logger.getLogger(ProcessingResultService.class);

    @Inject
    PhotoProcessingSessionRepository sessionRepository;

    @Inject
    ImageRepository imageRepository;

    @Inject
    DetectionRepository detectionRepository;

    @Inject
    ClassificationRepository classificationRepository;

    @Inject
    EstimationRepository estimationRepository;

    /**
     * Process and persist ML Worker results.
     *
     * @param request Processing results from ML Worker
     * @return Updated session status
     */
    @Transactional
    public SessionStatusDTO processResults(ProcessingResultRequest request) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(request.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", request.sessionId()));

        Image image = imageRepository.findByIdOptional(request.imageId())
                .orElseThrow(() -> new EntityNotFoundException("Image", request.imageId()));

        // Validate image belongs to session
        if (!image.getSession().getId().equals(session.getId())) {
            throw new IllegalArgumentException("Image does not belong to the specified session");
        }

        // Persist detections
        Map<Integer, UUID> detectionIdMap = new HashMap<>();
        if (request.detections() != null) {
            for (int i = 0; i < request.detections().size(); i++) {
                DetectionResultItem item = request.detections().get(i);
                Detection detection = createDetection(image, item);
                detectionRepository.persist(detection);
                detectionIdMap.put(i, detection.getId());
            }
            log.infof("Persisted %d detections for image %s", request.detections().size(), image.getId());
        }

        // Persist classifications
        if (request.classifications() != null) {
            for (ClassificationResultItem item : request.classifications()) {
                Classification classification = createClassification(image, item);
                classificationRepository.persist(classification);
            }
            log.infof("Persisted %d classifications for image %s", request.classifications().size(), image.getId());
        }

        // Persist estimations
        if (request.estimations() != null) {
            for (EstimationResultItem item : request.estimations()) {
                Estimation estimation = createEstimation(session, item);
                estimationRepository.persist(estimation);
            }
            log.infof("Persisted %d estimations for session %s", request.estimations().size(), session.getId());
        }

        // Update session progress
        session.setProcessedImages(session.getProcessedImages() + 1);

        if (session.getStatus() == ProcessingStatus.PENDING) {
            session.setStatus(ProcessingStatus.PROCESSING);
        }

        // Check if all images processed
        if (session.getProcessedImages() >= session.getTotalImages()) {
            session.setStatus(ProcessingStatus.COMPLETED);
            log.infof("Session %s completed: %d/%d images processed",
                    session.getId(), session.getProcessedImages(), session.getTotalImages());
        }

        return SessionStatusDTO.from(session);
    }

    /**
     * Mark a session as failed due to processing error.
     *
     * @param sessionId Session ID
     * @param imageId Image ID that failed
     * @param errorMessage Error description
     * @return Updated session status
     */
    @Transactional
    public SessionStatusDTO markFailed(UUID sessionId, UUID imageId, String errorMessage) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));

        session.setStatus(ProcessingStatus.FAILED);
        log.warnf("Session %s marked as failed: image=%s, error=%s", sessionId, imageId, errorMessage);

        return SessionStatusDTO.from(session);
    }

    private Detection createDetection(Image image, DetectionResultItem item) {
        Detection detection = new Detection();
        detection.setImage(image);
        detection.setLabel(item.label());
        detection.setConfidence(BigDecimal.valueOf(item.confidence()));

        if (item.boundingBox() != null) {
            Map<String, Object> bbox = new HashMap<>();
            bbox.put("x1", item.boundingBox().x1());
            bbox.put("y1", item.boundingBox().y1());
            bbox.put("x2", item.boundingBox().x2());
            bbox.put("y2", item.boundingBox().y2());
            detection.setBoundingBox(bbox);
        }

        return detection;
    }

    private Classification createClassification(Image image, ClassificationResultItem item) {
        Classification classification = new Classification();
        classification.setImage(image);
        classification.setCategory(item.label());
        classification.setConfidence(BigDecimal.valueOf(item.confidence()));
        return classification;
    }

    private Estimation createEstimation(PhotoProcessingSession session, EstimationResultItem item) {
        Estimation estimation = new Estimation();
        estimation.setSession(session);
        estimation.setEstimationType(item.estimationType());
        estimation.setValue(BigDecimal.valueOf(item.value()));
        estimation.setUnit(item.unit());
        if (item.confidence() != null) {
            estimation.setConfidence(BigDecimal.valueOf(item.confidence()));
        }
        return estimation;
    }
}
