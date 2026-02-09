package com.fortytwo.demeter.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCategoryRequest(
    @NotBlank @Size(max = 255) String name,
    String description,
    UUID parentId
) {}
