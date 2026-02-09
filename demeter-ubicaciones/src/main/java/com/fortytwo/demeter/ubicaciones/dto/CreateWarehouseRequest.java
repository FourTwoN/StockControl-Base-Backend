package com.fortytwo.demeter.ubicaciones.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateWarehouseRequest(@NotBlank String name, String address, BigDecimal latitude, BigDecimal longitude) {}
