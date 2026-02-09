package com.fortytwo.demeter.fotos.service;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.fotos.dto.ClassificationDTO;
import com.fortytwo.demeter.fotos.dto.CreateImageRequest;
import com.fortytwo.demeter.fotos.dto.DetectionDTO;
import com.fortytwo.demeter.fotos.dto.S3ImageDTO;
import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.model.S3Image;
import com.fortytwo.demeter.fotos.repository.ClassificationRepository;
import com.fortytwo.demeter.fotos.repository.DetectionRepository;
import com.fortytwo.demeter.fotos.repository.PhotoProcessingSessionRepository;
import com.fortytwo.demeter.fotos.repository.S3ImageRepository;
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
    S3ImageRepository imageRepository;

    @Inject
    PhotoProcessingSessionRepository sessionRepository;

    @Inject
    DetectionRepository detectionRepository;

    @Inject
    ClassificationRepository classificationRepository;

    @Transactional
    public S3ImageDTO addImage(UUID sessionId, CreateImageRequest request) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));

        S3Image image = new S3Image();
        image.setSession(session);
        image.setStorageUrl(request.storageUrl());
        image.setThumbnailUrl(request.thumbnailUrl());
        image.setOriginalFilename(request.originalFilename());
        image.setFileSize(request.fileSize());
        image.setMimeType(request.mimeType());

        imageRepository.persist(image);
        session.setTotalImages(session.getTotalImages() + 1);

        log.infof("Added image %s to session %s", image.getId(), sessionId);
        return S3ImageDTO.from(image);
    }

    public List<S3ImageDTO> findBySession(UUID sessionId) {
        sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        return imageRepository.findBySessionId(sessionId)
                .stream().map(S3ImageDTO::from).toList();
    }

    public S3ImageDTO findById(UUID id) {
        S3Image image = imageRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("S3Image", id));
        return S3ImageDTO.from(image);
    }

    public List<DetectionDTO> findDetectionsByImageId(UUID imageId) {
        imageRepository.findByIdOptional(imageId)
                .orElseThrow(() -> new EntityNotFoundException("S3Image", imageId));
        return detectionRepository.findByImageId(imageId)
                .stream().map(DetectionDTO::from).toList();
    }

    public List<ClassificationDTO> findClassificationsByImageId(UUID imageId) {
        imageRepository.findByIdOptional(imageId)
                .orElseThrow(() -> new EntityNotFoundException("S3Image", imageId));
        return classificationRepository.findByImageId(imageId)
                .stream().map(ClassificationDTO::from).toList();
    }
}
