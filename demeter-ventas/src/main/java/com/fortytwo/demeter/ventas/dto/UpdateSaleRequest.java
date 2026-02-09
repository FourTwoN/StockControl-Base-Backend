package com.fortytwo.demeter.ventas.dto;

import jakarta.validation.constraints.Size;

public record UpdateSaleRequest(
    @Size(max = 255) String customerName,
    @Size(max = 255) String customerEmail,
    String notes
) {}
