package com.fortytwo.demeter.fotos.dto;

import com.fortytwo.demeter.fotos.model.S3Image;
import java.time.Instant;
import java.util.UUID;

public record S3ImageDTO(
    UUID id,
    UUID sessionId,
    String storageUrl,
    String thumbnailUrl,
    String originalFilename,
    Long fileSize,
    String mimeType,
    int detectionCount,
    int classificationCount,
    Instant createdAt,
    Instant updatedAt
) {
    public static S3ImageDTO from(S3Image img) {
        return new S3ImageDTO(
            img.getId(),
            img.getSession() != null ? img.getSession().getId() : null,
            img.getStorageUrl(),
            img.getThumbnailUrl(),
            img.getOriginalFilename(),
            img.getFileSize(),
            img.getMimeType(),
            img.getDetections() != null ? img.getDetections().size() : 0,
            img.getClassifications() != null ? img.getClassifications().size() : 0,
            img.getCreatedAt(),
            img.getUpdatedAt()
        );
    }
}
