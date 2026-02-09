package com.fortytwo.demeter.productos.dto;

import com.fortytwo.demeter.productos.model.ProductFamily;
import java.time.Instant;
import java.util.UUID;

public record FamilyDTO(
    UUID id,
    String name,
    String description,
    Instant createdAt
) {
    public static FamilyDTO from(ProductFamily f) {
        return new FamilyDTO(f.getId(), f.getName(), f.getDescription(), f.getCreatedAt());
    }
}
