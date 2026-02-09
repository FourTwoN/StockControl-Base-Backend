package com.fortytwo.demeter.costos.dto;

import com.fortytwo.demeter.costos.model.Cost;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CostDTO(
    UUID id,
    UUID productId,
    UUID batchId,
    String costType,
    BigDecimal amount,
    String currency,
    String description,
    LocalDate effectiveDate,
    Instant createdAt,
    Instant updatedAt
) {
    public static CostDTO from(Cost c) {
        return new CostDTO(
            c.getId(),
            c.getProductId(),
            c.getBatchId(),
            c.getCostType(),
            c.getAmount(),
            c.getCurrency(),
            c.getDescription(),
            c.getEffectiveDate(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }
}
