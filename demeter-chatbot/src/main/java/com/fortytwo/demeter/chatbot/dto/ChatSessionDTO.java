package com.fortytwo.demeter.chatbot.dto;

import com.fortytwo.demeter.chatbot.model.ChatSession;
import java.time.Instant;
import java.util.UUID;

public record ChatSessionDTO(
    UUID id,
    UUID userId,
    String title,
    boolean active,
    int messageCount,
    Instant createdAt,
    Instant updatedAt
) {
    public static ChatSessionDTO from(ChatSession session) {
        return new ChatSessionDTO(
            session.getId(),
            session.getUserId(),
            session.getTitle(),
            session.isActive(),
            session.getMessages() != null ? session.getMessages().size() : 0,
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }
}
