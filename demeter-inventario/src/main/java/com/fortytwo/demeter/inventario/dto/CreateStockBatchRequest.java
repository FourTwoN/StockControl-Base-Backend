package com.fortytwo.demeter.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CreateStockBatchRequest(
    @NotNull UUID productId,
    @NotBlank @Size(max = 100) String batchCode,
    @NotNull @DecimalMin("0") BigDecimal quantity,
    @Size(max = 50) String unit,
    UUID warehouseId,
    UUID binId,
    Map<String, Object> customAttributes,
    Instant entryDate,
    Instant expiryDate
) {}
