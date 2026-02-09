package com.fortytwo.demeter.empaquetado.dto;

import com.fortytwo.demeter.empaquetado.model.PackagingCatalog;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PackagingCatalogDTO(UUID id, String name, UUID typeId, String typeName, UUID materialId, String materialName, UUID colorId, String colorName, BigDecimal capacity, String unit, Instant createdAt) {
    public static PackagingCatalogDTO from(PackagingCatalog c) {
        return new PackagingCatalogDTO(c.getId(), c.getName(),
            c.getType() != null ? c.getType().getId() : null, c.getType() != null ? c.getType().getName() : null,
            c.getMaterial() != null ? c.getMaterial().getId() : null, c.getMaterial() != null ? c.getMaterial().getName() : null,
            c.getColor() != null ? c.getColor().getId() : null, c.getColor() != null ? c.getColor().getName() : null,
            c.getCapacity(), c.getUnit(), c.getCreatedAt());
    }
}
