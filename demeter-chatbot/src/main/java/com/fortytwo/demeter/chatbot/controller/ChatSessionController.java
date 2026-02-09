package com.fortytwo.demeter.chatbot.controller;

import com.fortytwo.demeter.chatbot.dto.ChatMessageDTO;
import com.fortytwo.demeter.chatbot.dto.ChatSessionDTO;
import com.fortytwo.demeter.chatbot.dto.CreateChatMessageRequest;
import com.fortytwo.demeter.chatbot.dto.CreateChatSessionRequest;
import com.fortytwo.demeter.chatbot.service.ChatSessionService;
import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.common.dto.PagedResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/chat/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatSessionController {

    @Inject
    ChatSessionService chatSessionService;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public PagedResponse<ChatSessionDTO> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return chatSessionService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public ChatSessionDTO getById(@PathParam("id") UUID id) {
        return chatSessionService.findById(id);
    }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER})
    public Response create(@Valid CreateChatSessionRequest request) {
        ChatSessionDTO created = chatSessionService.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("/{id}/close")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER})
    public ChatSessionDTO closeSession(@PathParam("id") UUID id) {
        return chatSessionService.closeSession(id);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) {
        chatSessionService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/messages")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<ChatMessageDTO> listMessages(@PathParam("id") UUID id) {
        return chatSessionService.findMessages(id);
    }

    @POST
    @Path("/{id}/messages")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER})
    public Response addMessage(@PathParam("id") UUID id, @Valid CreateChatMessageRequest request) {
        ChatMessageDTO created = chatSessionService.addMessage(id, request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
