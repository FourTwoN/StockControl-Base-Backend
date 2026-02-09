package com.fortytwo.demeter.chatbot.dto;

import com.fortytwo.demeter.chatbot.model.ChatMessage;
import java.time.Instant;
import java.util.UUID;

public record ChatMessageDTO(
    UUID id,
    UUID sessionId,
    String role,
    String content,
    Integer tokensUsed,
    int toolExecutionCount,
    Instant createdAt
) {
    public static ChatMessageDTO from(ChatMessage message) {
        return new ChatMessageDTO(
            message.getId(),
            message.getSession() != null ? message.getSession().getId() : null,
            message.getRole().name(),
            message.getContent(),
            message.getTokensUsed(),
            message.getToolExecutions() != null ? message.getToolExecutions().size() : 0,
            message.getCreatedAt()
        );
    }
}
