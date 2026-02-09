package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TopProductSales(
    UUID productId,
    String productName,
    String productSku,
    long totalSales,
    BigDecimal totalQuantitySold,
    BigDecimal totalRevenue
) {}
