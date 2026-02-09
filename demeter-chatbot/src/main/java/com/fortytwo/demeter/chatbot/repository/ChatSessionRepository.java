package com.fortytwo.demeter.chatbot.repository;

import com.fortytwo.demeter.chatbot.model.ChatSession;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ChatSessionRepository implements PanacheRepositoryBase<ChatSession, UUID> {

    public List<ChatSession> findByUserId(UUID userId) {
        return find("userId", userId).list();
    }

    public List<ChatSession> findActive() {
        return find("active", true).list();
    }

    public List<ChatSession> findActiveByUserId(UUID userId) {
        return find("userId = ?1 and active = true", userId).list();
    }
}
