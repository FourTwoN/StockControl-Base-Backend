package com.fortytwo.demeter.chatbot.repository;

import com.fortytwo.demeter.chatbot.model.ChatToolExecution;
import com.fortytwo.demeter.chatbot.model.ToolExecutionStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ChatToolExecutionRepository implements PanacheRepositoryBase<ChatToolExecution, UUID> {

    public List<ChatToolExecution> findByMessageId(UUID messageId) {
        return find("message.id", messageId).list();
    }

    public List<ChatToolExecution> findByStatus(ToolExecutionStatus status) {
        return find("status", status).list();
    }
}
