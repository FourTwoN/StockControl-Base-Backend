package com.fortytwo.demeter.ubicaciones.dto;

import com.fortytwo.demeter.ubicaciones.model.StorageLocation;
import java.time.Instant;
import java.util.UUID;

public record StorageLocationDTO(UUID id, UUID areaId, String name, String description, Instant createdAt) {
    public static StorageLocationDTO from(StorageLocation l) {
        return new StorageLocationDTO(l.getId(), l.getArea().getId(), l.getName(), l.getDescription(), l.getCreatedAt());
    }
}
