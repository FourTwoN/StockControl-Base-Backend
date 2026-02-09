package com.fortytwo.demeter.chatbot.service;

import com.fortytwo.demeter.chatbot.dto.ChatMessageDTO;
import com.fortytwo.demeter.chatbot.dto.ChatToolExecutionDTO;
import com.fortytwo.demeter.chatbot.dto.CreateToolExecutionRequest;
import com.fortytwo.demeter.chatbot.model.ChatMessage;
import com.fortytwo.demeter.chatbot.model.ChatToolExecution;
import com.fortytwo.demeter.chatbot.model.ToolExecutionStatus;
import com.fortytwo.demeter.chatbot.repository.ChatMessageRepository;
import com.fortytwo.demeter.chatbot.repository.ChatToolExecutionRepository;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ChatMessageService {

    @Inject
    ChatMessageRepository chatMessageRepository;

    @Inject
    ChatToolExecutionRepository chatToolExecutionRepository;

    public List<ChatMessageDTO> findBySession(UUID sessionId) {
        return chatMessageRepository.findBySessionId(sessionId)
                .stream().map(ChatMessageDTO::from).toList();
    }

    public ChatMessageDTO findById(UUID id) {
        ChatMessage message = chatMessageRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("ChatMessage", id));
        return ChatMessageDTO.from(message);
    }

    public List<ChatToolExecutionDTO> findToolExecutions(UUID messageId) {
        chatMessageRepository.findByIdOptional(messageId)
                .orElseThrow(() -> new EntityNotFoundException("ChatMessage", messageId));
        return chatToolExecutionRepository.findByMessageId(messageId)
                .stream().map(ChatToolExecutionDTO::from).toList();
    }

    @Transactional
    public ChatToolExecutionDTO addToolExecution(UUID messageId, CreateToolExecutionRequest request) {
        ChatMessage message = chatMessageRepository.findByIdOptional(messageId)
                .orElseThrow(() -> new EntityNotFoundException("ChatMessage", messageId));

        ChatToolExecution execution = new ChatToolExecution();
        execution.setMessage(message);
        execution.setToolName(request.toolName());
        execution.setInput(request.input());

        chatToolExecutionRepository.persist(execution);
        return ChatToolExecutionDTO.from(execution);
    }

    @Transactional
    public ChatToolExecutionDTO updateToolExecution(UUID executionId, ToolExecutionStatus status,
                                                     Map<String, Object> output, Integer durationMs) {
        ChatToolExecution execution = chatToolExecutionRepository.findByIdOptional(executionId)
                .orElseThrow(() -> new EntityNotFoundException("ChatToolExecution", executionId));

        execution.setStatus(status);
        if (output != null) {
            execution.setOutput(output);
        }
        if (durationMs != null) {
            execution.setDurationMs(durationMs);
        }

        return ChatToolExecutionDTO.from(execution);
    }
}
