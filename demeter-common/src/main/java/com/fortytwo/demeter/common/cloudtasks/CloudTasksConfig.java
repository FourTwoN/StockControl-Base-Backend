package com.fortytwo.demeter.common.cloudtasks;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.Optional;

/**
 * Configuration for Google Cloud Tasks integration.
 *
 * <p>Configuration properties:
 * <pre>
 * demeter.cloudtasks.enabled=true
 * demeter.cloudtasks.project-id=my-gcp-project
 * demeter.cloudtasks.location=us-central1
 * demeter.cloudtasks.ml-worker-url=https://ml-worker-xxx.run.app
 * demeter.cloudtasks.queue-name=ml-tasks
 * </pre>
 */
@ConfigMapping(prefix = "demeter.cloudtasks")
public interface CloudTasksConfig {

    /**
     * Enable/disable Cloud Tasks integration.
     * When disabled, tasks are not created (useful for local development).
     */
    @WithDefault("false")
    boolean enabled();

    /**
     * GCP project ID where Cloud Tasks queues are located.
     */
    Optional<String> projectId();

    /**
     * GCP region/location for Cloud Tasks (e.g., us-central1).
     */
    @WithDefault("us-central1")
    String location();

    /**
     * Base URL of the ML Worker Cloud Run service.
     */
    Optional<String> mlWorkerUrl();

    /**
     * Default queue name for ML processing tasks.
     */
    @WithDefault("ml-tasks")
    String queueName();

    /**
     * Service account email for OIDC authentication to ML Worker.
     * If not set, uses Application Default Credentials.
     */
    Optional<String> serviceAccountEmail();

    /**
     * Task timeout in seconds.
     */
    @WithDefault("1800")
    int taskTimeoutSeconds();
}
