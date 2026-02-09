package com.fortytwo.demeter.ubicaciones.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateStorageBinTypeRequest(@NotBlank String name, Integer capacity, String description) {}
