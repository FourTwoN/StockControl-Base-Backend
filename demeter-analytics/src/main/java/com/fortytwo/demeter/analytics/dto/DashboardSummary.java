package com.fortytwo.demeter.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
    long totalProducts,
    long activeBatches,
    long totalWarehouses,
    long pendingSales,
    long completedSalesToday,
    BigDecimal totalInventoryValue,
    List<MovementSummary> recentMovementsByType
) {}
