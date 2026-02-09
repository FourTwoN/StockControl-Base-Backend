package com.fortytwo.demeter.empaquetado.dto;

import com.fortytwo.demeter.empaquetado.model.PackagingColor;
import java.time.Instant;
import java.util.UUID;

public record PackagingColorDTO(UUID id, String name, String hexCode, Instant createdAt) {
    public static PackagingColorDTO from(PackagingColor c) {
        return new PackagingColorDTO(c.getId(), c.getName(), c.getHexCode(), c.getCreatedAt());
    }
}
