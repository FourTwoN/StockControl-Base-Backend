package com.fortytwo.demeter.empaquetado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePackagingColorRequest(@NotBlank String name, @Size(max = 7) String hexCode) {}
