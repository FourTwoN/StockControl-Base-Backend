package com.fortytwo.demeter.fotos.dto;

import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import java.time.Instant;
import java.util.UUID;

public record PhotoSessionDTO(
    UUID id,
    String status,
    UUID productId,
    UUID batchId,
    UUID uploadedBy,
    int totalImages,
    int processedImages,
    int imageCount,
    Instant createdAt,
    Instant updatedAt
) {
    public static PhotoSessionDTO from(PhotoProcessingSession s) {
        return new PhotoSessionDTO(
            s.getId(),
            s.getStatus().name(),
            s.getProductId(),
            s.getBatchId(),
            s.getUploadedBy(),
            s.getTotalImages(),
            s.getProcessedImages(),
            s.getImages() != null ? s.getImages().size() : 0,
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
