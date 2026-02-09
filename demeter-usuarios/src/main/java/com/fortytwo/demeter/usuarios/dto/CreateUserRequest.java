package com.fortytwo.demeter.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String email, @NotBlank String name, String role) {}
