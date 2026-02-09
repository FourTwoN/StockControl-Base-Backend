package com.fortytwo.demeter.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateStockMovementRequest(
    @NotNull String movementType,
    @NotNull @DecimalMin("0.01") BigDecimal quantity,
    @Size(max = 50) String unit,
    UUID referenceId,
    @Size(max = 100) String referenceType,
    String notes,
    UUID performedBy,
    Instant performedAt,
    @NotEmpty @Valid List<BatchQuantity> batchQuantities
) {
    public record BatchQuantity(
        @NotNull UUID batchId,
        @NotNull @DecimalMin("0.01") BigDecimal quantity
    ) {}
}
