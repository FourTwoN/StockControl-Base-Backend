package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MovementSummary(
    String movementType,
    long count,
    BigDecimal totalQuantity,
    Instant firstMovement,
    Instant lastMovement
) {}
