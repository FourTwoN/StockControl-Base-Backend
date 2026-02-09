package com.fortytwo.demeter.chatbot.controller;

import com.fortytwo.demeter.chatbot.dto.ChatMessageDTO;
import com.fortytwo.demeter.chatbot.dto.ChatToolExecutionDTO;
import com.fortytwo.demeter.chatbot.dto.CreateToolExecutionRequest;
import com.fortytwo.demeter.chatbot.service.ChatMessageService;
import com.fortytwo.demeter.common.auth.RoleConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/chat/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatMessageController {

    @Inject
    ChatMessageService chatMessageService;

    @GET
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public ChatMessageDTO getById(@PathParam("id") UUID id) {
        return chatMessageService.findById(id);
    }

    @GET
    @Path("/{id}/tool-executions")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<ChatToolExecutionDTO> listToolExecutions(@PathParam("id") UUID id) {
        return chatMessageService.findToolExecutions(id);
    }

    @POST
    @Path("/{id}/tool-executions")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER})
    public Response addToolExecution(@PathParam("id") UUID id, @Valid CreateToolExecutionRequest request) {
        ChatToolExecutionDTO created = chatMessageService.addToolExecution(id, request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
