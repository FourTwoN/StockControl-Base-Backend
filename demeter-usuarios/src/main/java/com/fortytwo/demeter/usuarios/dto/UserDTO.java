package com.fortytwo.demeter.usuarios.dto;

import com.fortytwo.demeter.usuarios.model.User;
import java.time.Instant;
import java.util.UUID;

public record UserDTO(UUID id, String externalId, String email, String name, String role, boolean active, Instant createdAt, Instant updatedAt) {
    public static UserDTO from(User u) {
        return new UserDTO(u.getId(), u.getExternalId(), u.getEmail(), u.getName(), u.getRole().name(), u.isActive(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
