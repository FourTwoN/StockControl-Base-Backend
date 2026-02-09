package com.fortytwo.demeter.ubicaciones.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStorageAreaRequest(@NotBlank String name, String description) {}
