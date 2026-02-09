package com.fortytwo.demeter.chatbot.model;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatToolExecution> toolExecutions = new ArrayList<>();

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public ChatSession getSession() { return session; }
    public MessageRole getRole() { return role; }
    public String getContent() { return content; }
    public Integer getTokensUsed() { return tokensUsed; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ChatToolExecution> getToolExecutions() { return toolExecutions; }

    // Setters
    public void setSession(ChatSession session) { this.session = session; }
    public void setRole(MessageRole role) { this.role = role; }
    public void setContent(String content) { this.content = content; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }

    // Only for testing / framework use
    protected void setId(UUID id) { this.id = id; }
}
