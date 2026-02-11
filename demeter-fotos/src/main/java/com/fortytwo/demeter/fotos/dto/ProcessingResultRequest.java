package com.fortytwo.demeter.fotos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Request payload received from ML Worker with processing results.
 *
 * <p>The ML Worker sends this payload to report detections, classifications,
 * and estimations after processing an image.
 */
public record ProcessingResultRequest(
        @NotNull UUID sessionId,
        @NotNull UUID imageId,
        @Valid List<DetectionResultItem> detections,
        @Valid List<ClassificationResultItem> classifications,
        @Valid List<EstimationResultItem> estimations,
        ProcessingMetadata metadata
) {
    /**
     * Single detection result from ML inference.
     */
    public record DetectionResultItem(
            @NotNull String label,
            @NotNull Double confidence,
            BoundingBox boundingBox
    ) {}

    /**
     * Single classification result from ML inference.
     */
    public record ClassificationResultItem(
            @NotNull String label,
            @NotNull Double confidence,
            UUID detectionId
    ) {}

    /**
     * Estimation result (count, area, etc.) from ML inference.
     */
    public record EstimationResultItem(
            @NotNull String estimationType,
            @NotNull Double value,
            String unit,
            Double confidence
    ) {}

    /**
     * Bounding box coordinates (normalized 0-1 or pixel values).
     */
    public record BoundingBox(
            double x1,
            double y1,
            double x2,
            double y2
    ) {}

    /**
     * Processing metadata from ML Worker.
     */
    public record ProcessingMetadata(
            String pipeline,
            Long processingTimeMs,
            String modelVersion,
            String workerVersion
    ) {}
}
