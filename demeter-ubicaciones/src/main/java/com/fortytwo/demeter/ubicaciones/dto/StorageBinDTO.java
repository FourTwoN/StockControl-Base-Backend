package com.fortytwo.demeter.ubicaciones.dto;

import com.fortytwo.demeter.ubicaciones.model.StorageBin;
import java.time.Instant;
import java.util.UUID;

public record StorageBinDTO(UUID id, UUID locationId, UUID binTypeId, String code, boolean occupied, Instant createdAt) {
    public static StorageBinDTO from(StorageBin b) {
        return new StorageBinDTO(b.getId(), b.getLocation().getId(), b.getBinType() != null ? b.getBinType().getId() : null, b.getCode(), b.isOccupied(), b.getCreatedAt());
    }
}
