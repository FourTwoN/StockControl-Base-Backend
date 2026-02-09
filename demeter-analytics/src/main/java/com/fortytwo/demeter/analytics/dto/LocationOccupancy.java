package com.fortytwo.demeter.analytics.dto;

import java.util.UUID;

public record LocationOccupancy(
    UUID warehouseId,
    String warehouseName,
    long totalBins,
    long occupiedBins,
    double occupancyRate
) {}
