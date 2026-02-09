package com.fortytwo.demeter.chatbot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateChatSessionRequest(
    @NotNull UUID userId,
    @Size(max = 500) String title
) {}
