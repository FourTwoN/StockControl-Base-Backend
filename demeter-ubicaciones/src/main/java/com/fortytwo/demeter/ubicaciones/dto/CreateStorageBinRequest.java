package com.fortytwo.demeter.ubicaciones.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateStorageBinRequest(@NotBlank String code, UUID binTypeId) {}
