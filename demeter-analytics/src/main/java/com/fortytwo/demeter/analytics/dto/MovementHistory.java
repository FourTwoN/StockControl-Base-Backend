package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MovementHistory(
    UUID movementId,
    String movementType,
    BigDecimal quantity,
    String unit,
    String notes,
    UUID performedBy,
    Instant performedAt,
    List<BatchMovementDetail> batches
) {}
