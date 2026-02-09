package com.fortytwo.demeter.empaquetado.dto;

import com.fortytwo.demeter.empaquetado.model.PackagingType;
import java.time.Instant;
import java.util.UUID;

public record PackagingTypeDTO(UUID id, String name, String description, Instant createdAt) {
    public static PackagingTypeDTO from(PackagingType t) {
        return new PackagingTypeDTO(t.getId(), t.getName(), t.getDescription(), t.getCreatedAt());
    }
}
