package com.fortytwo.demeter.ubicaciones.dto;

import com.fortytwo.demeter.ubicaciones.model.StorageArea;
import java.time.Instant;
import java.util.UUID;

public record StorageAreaDTO(UUID id, UUID warehouseId, String name, String description, Instant createdAt) {
    public static StorageAreaDTO from(StorageArea a) {
        return new StorageAreaDTO(a.getId(), a.getWarehouse().getId(), a.getName(), a.getDescription(), a.getCreatedAt());
    }
}
