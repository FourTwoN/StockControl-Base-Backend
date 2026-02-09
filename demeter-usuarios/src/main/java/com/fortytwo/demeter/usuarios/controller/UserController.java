package com.fortytwo.demeter.usuarios.controller;

import com.fortytwo.demeter.common.auth.CurrentUser;
import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.usuarios.dto.*;
import com.fortytwo.demeter.usuarios.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject UserService userService;
    @Inject CurrentUser currentUser;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public PagedResponse<UserDTO> list(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("size") @DefaultValue("20") int size) {
        return userService.findAll(page, size);
    }

    @GET
    @Path("/me")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public UserDTO me() {
        return userService.findOrCreateFromExternalId(currentUser.getUserId(), currentUser.getEmail(), currentUser.getName());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public UserDTO getById(@PathParam("id") UUID id) {
        return userService.findById(id);
    }

    @POST
    @RolesAllowed({RoleConstants.ADMIN})
    public Response create(@Valid CreateUserRequest req) {
        return Response.status(Response.Status.CREATED).entity(userService.create(req)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public UserDTO update(@PathParam("id") UUID id, @Valid UpdateUserRequest req) {
        return userService.update(id, req);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) {
        userService.delete(id);
        return Response.noContent().build();
    }
}
