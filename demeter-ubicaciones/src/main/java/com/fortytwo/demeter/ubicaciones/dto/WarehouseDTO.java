package com.fortytwo.demeter.ubicaciones.dto;

import com.fortytwo.demeter.ubicaciones.model.Warehouse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WarehouseDTO(UUID id, String name, String address, BigDecimal latitude, BigDecimal longitude, boolean active, Instant createdAt) {
    public static WarehouseDTO from(Warehouse w) {
        return new WarehouseDTO(w.getId(), w.getName(), w.getAddress(), w.getLatitude(), w.getLongitude(), w.isActive(), w.getCreatedAt());
    }
}
