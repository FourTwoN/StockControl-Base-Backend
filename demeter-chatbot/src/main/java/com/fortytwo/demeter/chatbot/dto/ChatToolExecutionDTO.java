package com.fortytwo.demeter.chatbot.dto;

import com.fortytwo.demeter.chatbot.model.ChatToolExecution;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ChatToolExecutionDTO(
    UUID id,
    UUID messageId,
    String toolName,
    Map<String, Object> input,
    Map<String, Object> output,
    String status,
    Integer durationMs,
    Instant createdAt
) {
    public static ChatToolExecutionDTO from(ChatToolExecution execution) {
        return new ChatToolExecutionDTO(
            execution.getId(),
            execution.getMessage() != null ? execution.getMessage().getId() : null,
            execution.getToolName(),
            execution.getInput(),
            execution.getOutput(),
            execution.getStatus().name(),
            execution.getDurationMs(),
            execution.getCreatedAt()
        );
    }
}
