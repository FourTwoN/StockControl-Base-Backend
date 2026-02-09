package com.fortytwo.demeter.chatbot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "chat_tool_executions")
public class ChatToolExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @Column(name = "tool_name", nullable = false)
    private String toolName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> input;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> output;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ToolExecutionStatus status = ToolExecutionStatus.PENDING;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public ChatMessage getMessage() { return message; }
    public String getToolName() { return toolName; }
    public Map<String, Object> getInput() { return input; }
    public Map<String, Object> getOutput() { return output; }
    public ToolExecutionStatus getStatus() { return status; }
    public Integer getDurationMs() { return durationMs; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setMessage(ChatMessage message) { this.message = message; }
    public void setToolName(String toolName) { this.toolName = toolName; }
    public void setInput(Map<String, Object> input) { this.input = input; }
    public void setOutput(Map<String, Object> output) { this.output = output; }
    public void setStatus(ToolExecutionStatus status) { this.status = status; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    // Only for testing / framework use
    protected void setId(UUID id) { this.id = id; }
}
