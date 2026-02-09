package com.fortytwo.demeter.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UpdateStockBatchRequest(
    @Size(max = 50) String unit,
    UUID warehouseId,
    UUID binId,
    String status,
    Map<String, Object> customAttributes,
    Instant expiryDate
) {}
