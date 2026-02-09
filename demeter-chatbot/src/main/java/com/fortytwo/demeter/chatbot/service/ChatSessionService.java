package com.fortytwo.demeter.chatbot.service;

import com.fortytwo.demeter.chatbot.dto.ChatMessageDTO;
import com.fortytwo.demeter.chatbot.dto.ChatSessionDTO;
import com.fortytwo.demeter.chatbot.dto.CreateChatMessageRequest;
import com.fortytwo.demeter.chatbot.dto.CreateChatSessionRequest;
import com.fortytwo.demeter.chatbot.model.ChatMessage;
import com.fortytwo.demeter.chatbot.model.ChatSession;
import com.fortytwo.demeter.chatbot.model.MessageRole;
import com.fortytwo.demeter.chatbot.repository.ChatMessageRepository;
import com.fortytwo.demeter.chatbot.repository.ChatSessionRepository;
import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ChatSessionService {

    @Inject
    ChatSessionRepository chatSessionRepository;

    @Inject
    ChatMessageRepository chatMessageRepository;

    public PagedResponse<ChatSessionDTO> findAll(int page, int size) {
        var query = chatSessionRepository.findAll();
        var sessions = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = sessions.stream().map(ChatSessionDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public ChatSessionDTO findById(UUID id) {
        ChatSession session = chatSessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("ChatSession", id));
        return ChatSessionDTO.from(session);
    }

    public List<ChatSessionDTO> findByUserId(UUID userId) {
        return chatSessionRepository.findByUserId(userId)
                .stream().map(ChatSessionDTO::from).toList();
    }

    public List<ChatSessionDTO> findActiveByUserId(UUID userId) {
        return chatSessionRepository.findActiveByUserId(userId)
                .stream().map(ChatSessionDTO::from).toList();
    }

    @Transactional
    public ChatSessionDTO create(CreateChatSessionRequest request) {
        ChatSession session = new ChatSession();
        session.setUserId(request.userId());
        session.setTitle(request.title());

        chatSessionRepository.persist(session);
        return ChatSessionDTO.from(session);
    }

    @Transactional
    public ChatSessionDTO closeSession(UUID id) {
        ChatSession session = chatSessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("ChatSession", id));
        session.setActive(false);
        return ChatSessionDTO.from(session);
    }

    @Transactional
    public void delete(UUID id) {
        ChatSession session = chatSessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("ChatSession", id));
        chatSessionRepository.delete(session);
    }

    public List<ChatMessageDTO> findMessages(UUID sessionId) {
        chatSessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("ChatSession", sessionId));
        return chatMessageRepository.findBySessionId(sessionId)
                .stream().map(ChatMessageDTO::from).toList();
    }

    @Transactional
    public ChatMessageDTO addMessage(UUID sessionId, CreateChatMessageRequest request) {
        ChatSession session = chatSessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("ChatSession", sessionId));

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(MessageRole.valueOf(request.role()));
        message.setContent(request.content());

        chatMessageRepository.persist(message);
        return ChatMessageDTO.from(message);
    }
}
