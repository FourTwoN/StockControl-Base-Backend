package com.fortytwo.demeter.empaquetado.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePackagingTypeRequest(@NotBlank String name, String description) {}
