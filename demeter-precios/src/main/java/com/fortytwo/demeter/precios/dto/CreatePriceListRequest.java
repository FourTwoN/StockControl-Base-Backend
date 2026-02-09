package com.fortytwo.demeter.precios.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record CreatePriceListRequest(
    @NotBlank @Size(max = 255) String name,
    String description,
    @NotNull LocalDate effectiveDate,
    List<@Valid CreatePriceEntryRequest> entries
) {}
