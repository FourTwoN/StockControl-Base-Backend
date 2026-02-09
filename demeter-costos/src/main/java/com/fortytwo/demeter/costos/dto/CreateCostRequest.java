package com.fortytwo.demeter.costos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateCostRequest(
    @NotNull UUID productId,
    UUID batchId,
    @NotBlank @Size(max = 100) String costType,
    @NotNull @DecimalMin("0") BigDecimal amount,
    @Size(max = 3) String currency,
    String description,
    @NotNull LocalDate effectiveDate
) {}
