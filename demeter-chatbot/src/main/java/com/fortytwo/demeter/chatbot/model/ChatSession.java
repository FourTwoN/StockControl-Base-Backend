package com.fortytwo.demeter.chatbot.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(length = 500)
    private String title;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // Getters
    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public boolean isActive() { return active; }
    public List<ChatMessage> getMessages() { return messages; }

    // Setters
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setActive(boolean active) { this.active = active; }
}
