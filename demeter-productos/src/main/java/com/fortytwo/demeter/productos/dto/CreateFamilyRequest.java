package com.fortytwo.demeter.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFamilyRequest(
    @NotBlank @Size(max = 255) String name,
    String description
) {}
