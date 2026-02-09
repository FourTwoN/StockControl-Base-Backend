package com.fortytwo.demeter.productos.dto;

import com.fortytwo.demeter.productos.model.Product;
import java.time.Instant;
import java.util.UUID;

public record ProductDTO(
    UUID id,
    String sku,
    String name,
    String description,
    UUID categoryId,
    String categoryName,
    UUID familyId,
    String familyName,
    String state,
    Instant createdAt,
    Instant updatedAt
) {
    public static ProductDTO from(Product p) {
        return new ProductDTO(
            p.getId(),
            p.getSku(),
            p.getName(),
            p.getDescription(),
            p.getCategory() != null ? p.getCategory().getId() : null,
            p.getCategory() != null ? p.getCategory().getName() : null,
            p.getFamily() != null ? p.getFamily().getId() : null,
            p.getFamily() != null ? p.getFamily().getName() : null,
            p.getState().name(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }
}
