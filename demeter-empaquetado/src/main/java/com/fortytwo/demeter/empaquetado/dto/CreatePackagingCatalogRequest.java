package com.fortytwo.demeter.empaquetado.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record CreatePackagingCatalogRequest(@NotBlank String name, UUID typeId, UUID materialId, UUID colorId, BigDecimal capacity, String unit) {}
