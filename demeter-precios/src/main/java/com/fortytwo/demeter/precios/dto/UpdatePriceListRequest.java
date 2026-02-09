package com.fortytwo.demeter.precios.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePriceListRequest(
    @Size(max = 255) String name,
    String description,
    LocalDate effectiveDate
) {}
