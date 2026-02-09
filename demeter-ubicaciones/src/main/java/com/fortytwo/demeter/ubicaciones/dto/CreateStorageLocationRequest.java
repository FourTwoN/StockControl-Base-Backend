package com.fortytwo.demeter.ubicaciones.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStorageLocationRequest(@NotBlank String name, String description) {}
