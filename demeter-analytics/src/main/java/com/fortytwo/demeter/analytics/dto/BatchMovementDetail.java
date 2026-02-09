package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BatchMovementDetail(
    UUID batchId,
    String batchCode,
    BigDecimal quantity
) {}
