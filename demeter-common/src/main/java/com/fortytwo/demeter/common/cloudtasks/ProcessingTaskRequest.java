package com.fortytwo.demeter.common.cloudtasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.UUID;

/**
 * Request payload sent to ML Worker via Cloud Tasks.
 *
 * <p>This record matches the schema expected by the ML Worker's
 * /tasks/process endpoint (ProcessingRequest in Python).
 *
 * <p>Uses snake_case JSON serialization to match Python conventions.
 *
 * @param tenantId Tenant identifier for multi-tenant isolation
 * @param sessionId Photo processing session ID
 * @param imageId Image ID to process
 * @param imageUrl Cloud Storage URL (gs://bucket/path/to/image.jpg)
 * @param pipeline Pipeline name from industry config (e.g., "DETECTION", "FULL_PIPELINE")
 * @param options Optional processing parameters
 * @param callbackUrl Optional callback URL for results notification
 */
public record ProcessingTaskRequest(
        @JsonProperty("tenant_id") String tenantId,
        @JsonProperty("session_id") UUID sessionId,
        @JsonProperty("image_id") UUID imageId,
        @JsonProperty("image_url") String imageUrl,
        String pipeline,
        Map<String, Object> options,
        @JsonProperty("callback_url") String callbackUrl
) {
    /**
     * Create request with default pipeline.
     */
    public static ProcessingTaskRequest of(
            String tenantId,
            UUID sessionId,
            UUID imageId,
            String imageUrl
    ) {
        return new ProcessingTaskRequest(
                tenantId,
                sessionId,
                imageId,
                imageUrl,
                "DETECTION",
                null,
                null
        );
    }

    /**
     * Create request with specific pipeline.
     */
    public static ProcessingTaskRequest of(
            String tenantId,
            UUID sessionId,
            UUID imageId,
            String imageUrl,
            String pipeline
    ) {
        return new ProcessingTaskRequest(
                tenantId,
                sessionId,
                imageId,
                imageUrl,
                pipeline,
                null,
                null
        );
    }

    /**
     * Create request with pipeline and callback URL.
     */
    public static ProcessingTaskRequest of(
            String tenantId,
            UUID sessionId,
            UUID imageId,
            String imageUrl,
            String pipeline,
            String callbackUrl
    ) {
        return new ProcessingTaskRequest(
                tenantId,
                sessionId,
                imageId,
                imageUrl,
                pipeline,
                null,
                callbackUrl
        );
    }
}
