package com.fortytwo.demeter.fotos.dto;

import java.util.UUID;

public record CreatePhotoSessionRequest(
    UUID productId,
    UUID batchId
) {}
