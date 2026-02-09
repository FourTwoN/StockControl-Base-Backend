package com.fortytwo.demeter.common.exception;

import java.time.Instant;

public record ErrorResponse(
    int status,
    String message,
    String detail,
    Instant timestamp
) {
    public ErrorResponse(int status, String message, String detail) {
        this(status, message, detail, Instant.now());
    }
}
