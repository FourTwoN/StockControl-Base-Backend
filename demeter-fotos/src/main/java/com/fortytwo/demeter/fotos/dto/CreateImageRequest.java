package com.fortytwo.demeter.fotos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateImageRequest(
    @NotBlank String storageUrl,
    String thumbnailUrl,
    @Size(max = 500) String originalFilename,
    Long fileSize,
    @Size(max = 100) String mimeType
) {}
