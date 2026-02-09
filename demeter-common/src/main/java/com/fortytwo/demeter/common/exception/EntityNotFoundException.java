package com.fortytwo.demeter.common.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final String entityId;

    public EntityNotFoundException(String entityType, UUID id) {
        super(entityType + " not found with id: " + id);
        this.entityType = entityType;
        this.entityId = id.toString();
    }

    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " not found: " + identifier);
        this.entityType = entityType;
        this.entityId = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
