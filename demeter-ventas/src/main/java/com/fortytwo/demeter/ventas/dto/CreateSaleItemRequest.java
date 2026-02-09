package com.fortytwo.demeter.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateSaleItemRequest(
    @NotNull UUID productId,
    UUID batchId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal quantity,
    @NotNull @DecimalMin(value = "0.00") BigDecimal unitPrice
) {}
