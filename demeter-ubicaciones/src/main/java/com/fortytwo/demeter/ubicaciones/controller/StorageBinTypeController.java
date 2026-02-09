package com.fortytwo.demeter.ubicaciones.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.service.StorageBinTypeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/api/v1/bin-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageBinTypeController {
    @Inject StorageBinTypeService service;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public PagedResponse<StorageBinTypeDTO> list(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("size") @DefaultValue("20") int size) {
        return service.findAll(page, size);
    }

    @GET @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public StorageBinTypeDTO getById(@PathParam("id") UUID id) { return service.findById(id); }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public Response create(@Valid CreateStorageBinTypeRequest req) {
        return Response.status(Response.Status.CREATED).entity(service.create(req)).build();
    }

    @DELETE @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) { service.delete(id); return Response.noContent().build(); }
}
