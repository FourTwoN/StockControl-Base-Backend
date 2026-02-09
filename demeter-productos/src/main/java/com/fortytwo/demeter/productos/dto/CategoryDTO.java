package com.fortytwo.demeter.productos.dto;

import com.fortytwo.demeter.productos.model.ProductCategory;
import java.time.Instant;
import java.util.UUID;

public record CategoryDTO(
    UUID id,
    String name,
    String description,
    UUID parentId,
    Instant createdAt
) {
    public static CategoryDTO from(ProductCategory c) {
        return new CategoryDTO(
            c.getId(),
            c.getName(),
            c.getDescription(),
            c.getParent() != null ? c.getParent().getId() : null,
            c.getCreatedAt()
        );
    }
}
