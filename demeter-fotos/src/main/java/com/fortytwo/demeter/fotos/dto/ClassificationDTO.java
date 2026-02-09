package com.fortytwo.demeter.fotos.dto;

import com.fortytwo.demeter.fotos.model.Classification;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ClassificationDTO(
    UUID id,
    UUID imageId,
    String category,
    BigDecimal confidence,
    Map<String, Object> metadata,
    Instant createdAt
) {
    public static ClassificationDTO from(Classification c) {
        return new ClassificationDTO(
            c.getId(),
            c.getImage() != null ? c.getImage().getId() : null,
            c.getCategory(),
            c.getConfidence(),
            c.getMetadata(),
            c.getCreatedAt()
        );
    }
}
