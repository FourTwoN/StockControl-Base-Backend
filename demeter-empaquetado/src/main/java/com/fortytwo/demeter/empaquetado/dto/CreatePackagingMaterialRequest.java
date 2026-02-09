package com.fortytwo.demeter.empaquetado.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePackagingMaterialRequest(@NotBlank String name, String description) {}
