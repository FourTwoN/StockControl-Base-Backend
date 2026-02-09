package com.fortytwo.demeter.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateSaleRequest(
    @Size(max = 255) String customerName,
    @Size(max = 255) String customerEmail,
    String notes,
    UUID soldBy,
    @NotEmpty @Valid List<CreateSaleItemRequest> items
) {}
