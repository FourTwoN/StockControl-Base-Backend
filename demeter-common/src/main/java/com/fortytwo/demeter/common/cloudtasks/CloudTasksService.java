package com.fortytwo.demeter.common.cloudtasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.HttpRequest;
import com.google.cloud.tasks.v2.OidcToken;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.io.IOException;
import java.util.UUID;

/**
 * Service for creating Cloud Tasks to dispatch ML processing jobs.
 *
 * <p>Integrates with Google Cloud Tasks to create HTTP tasks that invoke
 * the ML Worker Cloud Run service. Uses OIDC authentication for secure
 * service-to-service communication.
 *
 * <p>When disabled (local development), logs the task details without
 * actually creating the task.
 */
@ApplicationScoped
public class CloudTasksService {

    private static final Logger log = Logger.getLogger(CloudTasksService.class);
    private static final String CONTENT_TYPE = "application/json";
    private static final String PROCESS_ENDPOINT = "/tasks/process";

    @Inject
    CloudTasksConfig config;

    @Inject
    ObjectMapper objectMapper;

    private CloudTasksClient client;

    @PostConstruct
    void init() {
        if (config.enabled()) {
            try {
                client = CloudTasksClient.create();
                log.info("Cloud Tasks client initialized");
            } catch (IOException e) {
                log.error("Failed to create Cloud Tasks client", e);
                throw new RuntimeException("Cloud Tasks client initialization failed", e);
            }
        } else {
            log.info("Cloud Tasks disabled - tasks will be logged but not created");
        }
    }

    @PreDestroy
    void cleanup() {
        if (client != null) {
            client.close();
            log.info("Cloud Tasks client closed");
        }
    }

    /**
     * Create an ML processing task.
     *
     * <p>The task will be dispatched to the ML Worker service via Cloud Tasks.
     * If Cloud Tasks is disabled, logs the request without creating a task.
     *
     * @param request Processing request with image details
     * @return Task name if created, or null if disabled
     */
    public String createProcessingTask(ProcessingTaskRequest request) {
        return createProcessingTask(request, config.queueName());
    }

    /**
     * Create an ML processing task in a specific queue.
     *
     * @param request Processing request with image details
     * @param queueName Target queue name
     * @return Task name if created, or null if disabled
     */
    public String createProcessingTask(ProcessingTaskRequest request, String queueName) {
        if (!config.enabled()) {
            logDisabledTask(request, queueName);
            return null;
        }

        validateConfig();

        String projectId = config.projectId().orElseThrow();
        String mlWorkerUrl = config.mlWorkerUrl().orElseThrow();

        QueueName queue = QueueName.of(projectId, config.location(), queueName);
        String targetUrl = mlWorkerUrl + PROCESS_ENDPOINT;

        Task task = buildTask(request, targetUrl);
        Task createdTask = client.createTask(queue, task);

        log.infof("Created Cloud Task: %s for image %s in session %s",
                createdTask.getName(),
                request.imageId(),
                request.sessionId());

        return createdTask.getName();
    }

    /**
     * Create a task with custom endpoint.
     *
     * @param request Processing request
     * @param endpoint Custom endpoint path (e.g., "/tasks/compress")
     * @param queueName Target queue name
     * @return Task name if created, or null if disabled
     */
    public String createCustomTask(ProcessingTaskRequest request, String endpoint, String queueName) {
        if (!config.enabled()) {
            logDisabledTask(request, queueName);
            return null;
        }

        validateConfig();

        String projectId = config.projectId().orElseThrow();
        String mlWorkerUrl = config.mlWorkerUrl().orElseThrow();

        QueueName queue = QueueName.of(projectId, config.location(), queueName);
        String targetUrl = mlWorkerUrl + endpoint;

        Task task = buildTask(request, targetUrl);
        Task createdTask = client.createTask(queue, task);

        log.infof("Created Cloud Task: %s for endpoint %s", createdTask.getName(), endpoint);

        return createdTask.getName();
    }

    private Task buildTask(ProcessingTaskRequest request, String targetUrl) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize task payload", e);
        }

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                .setUrl(targetUrl)
                .setHttpMethod(HttpMethod.POST)
                .putHeaders("Content-Type", CONTENT_TYPE)
                .setBody(ByteString.copyFromUtf8(payload));

        // Add OIDC token for authentication
        config.serviceAccountEmail().ifPresent(email -> {
            OidcToken oidcToken = OidcToken.newBuilder()
                    .setServiceAccountEmail(email)
                    .setAudience(config.mlWorkerUrl().orElseThrow())
                    .build();
            httpRequestBuilder.setOidcToken(oidcToken);
        });

        Task.Builder taskBuilder = Task.newBuilder()
                .setHttpRequest(httpRequestBuilder.build())
                .setDispatchDeadline(Duration.newBuilder()
                        .setSeconds(config.taskTimeoutSeconds())
                        .build());

        // Use image ID as task name for idempotency
        if (request.imageId() != null) {
            String taskId = "img-" + request.imageId().toString().replace("-", "");
            String projectId = config.projectId().orElseThrow();
            String taskName = String.format(
                    "projects/%s/locations/%s/queues/%s/tasks/%s",
                    projectId,
                    config.location(),
                    config.queueName(),
                    taskId
            );
            taskBuilder.setName(taskName);
        }

        return taskBuilder.build();
    }

    private void validateConfig() {
        if (config.projectId().isEmpty()) {
            throw new IllegalStateException("Cloud Tasks project ID not configured");
        }
        if (config.mlWorkerUrl().isEmpty()) {
            throw new IllegalStateException("ML Worker URL not configured");
        }
    }

    private void logDisabledTask(ProcessingTaskRequest request, String queueName) {
        log.infof("[DISABLED] Would create Cloud Task in queue '%s': tenant=%s, session=%s, image=%s, pipeline=%s",
                queueName,
                request.tenantId(),
                request.sessionId(),
                request.imageId(),
                request.pipeline());
    }
}
