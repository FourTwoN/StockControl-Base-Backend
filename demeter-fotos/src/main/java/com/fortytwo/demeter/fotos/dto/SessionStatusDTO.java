package com.fortytwo.demeter.fotos.dto;

import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record SessionStatusDTO(
    UUID sessionId,
    String status,
    int totalImages,
    int processedImages,
    BigDecimal progress
) {
    public static SessionStatusDTO from(PhotoProcessingSession s) {
        BigDecimal progressPct = s.getTotalImages() > 0
            ? BigDecimal.valueOf(s.getProcessedImages())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(s.getTotalImages()), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return new SessionStatusDTO(
            s.getId(),
            s.getStatus().name(),
            s.getTotalImages(),
            s.getProcessedImages(),
            progressPct
        );
    }
}
