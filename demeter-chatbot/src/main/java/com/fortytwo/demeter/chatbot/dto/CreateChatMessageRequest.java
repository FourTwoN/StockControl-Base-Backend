package com.fortytwo.demeter.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateChatMessageRequest(
    @NotNull String role,
    @NotBlank String content
) {}
