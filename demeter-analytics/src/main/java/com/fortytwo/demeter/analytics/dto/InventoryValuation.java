package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryValuation(
    UUID productId,
    String productName,
    BigDecimal totalQuantity,
    BigDecimal averageCost,
    BigDecimal totalValue,
    String currency
) {}
