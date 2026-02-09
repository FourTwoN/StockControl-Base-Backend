package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StockSummary(
    UUID productId,
    String productName,
    String productSku,
    long activeBatches,
    BigDecimal totalQuantity,
    String unit
) {}
