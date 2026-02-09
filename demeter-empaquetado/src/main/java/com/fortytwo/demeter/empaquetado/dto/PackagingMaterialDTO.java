package com.fortytwo.demeter.empaquetado.dto;

import com.fortytwo.demeter.empaquetado.model.PackagingMaterial;
import java.time.Instant;
import java.util.UUID;

public record PackagingMaterialDTO(UUID id, String name, String description, Instant createdAt) {
    public static PackagingMaterialDTO from(PackagingMaterial m) {
        return new PackagingMaterialDTO(m.getId(), m.getName(), m.getDescription(), m.getCreatedAt());
    }
}
