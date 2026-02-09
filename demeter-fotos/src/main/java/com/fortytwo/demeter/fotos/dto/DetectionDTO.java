package com.fortytwo.demeter.fotos.dto;

import com.fortytwo.demeter.fotos.model.Detection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DetectionDTO(
    UUID id,
    UUID imageId,
    String label,
    BigDecimal confidence,
    Map<String, Object> boundingBox,
    Instant createdAt
) {
    public static DetectionDTO from(Detection d) {
        return new DetectionDTO(
            d.getId(),
            d.getImage() != null ? d.getImage().getId() : null,
            d.getLabel(),
            d.getConfidence(),
            d.getBoundingBox(),
            d.getCreatedAt()
        );
    }
}
