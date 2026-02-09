package com.fortytwo.demeter.ubicaciones.dto;

import com.fortytwo.demeter.ubicaciones.model.StorageBinType;
import java.time.Instant;
import java.util.UUID;

public record StorageBinTypeDTO(UUID id, String name, Integer capacity, String description, Instant createdAt) {
    public static StorageBinTypeDTO from(StorageBinType t) {
        return new StorageBinTypeDTO(t.getId(), t.getName(), t.getCapacity(), t.getDescription(), t.getCreatedAt());
    }
}
