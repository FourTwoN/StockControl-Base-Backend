package com.fortytwo.demeter.productos.dto;

import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateProductRequest(
    @Size(max = 255) String name,
    String description,
    UUID categoryId,
    UUID familyId,
    String state
) {}
