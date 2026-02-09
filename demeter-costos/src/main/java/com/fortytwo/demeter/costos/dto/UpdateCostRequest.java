package com.fortytwo.demeter.costos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateCostRequest(
    UUID productId,
    UUID batchId,
    @Size(max = 100) String costType,
    @DecimalMin("0") BigDecimal amount,
    @Size(max = 3) String currency,
    String description,
    LocalDate effectiveDate
) {}
