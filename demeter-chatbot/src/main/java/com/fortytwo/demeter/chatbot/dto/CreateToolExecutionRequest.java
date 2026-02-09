package com.fortytwo.demeter.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CreateToolExecutionRequest(
    @NotBlank String toolName,
    Map<String, Object> input
) {}
