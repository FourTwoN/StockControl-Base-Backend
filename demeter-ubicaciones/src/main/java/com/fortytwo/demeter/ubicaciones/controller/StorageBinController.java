package com.fortytwo.demeter.ubicaciones.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.service.StorageBinService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/locations/{locationId}/bins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageBinController {
    @Inject StorageBinService binService;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<StorageBinDTO> list(@PathParam("locationId") UUID locationId) { return binService.findByLocation(locationId); }

    @GET @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public StorageBinDTO getById(@PathParam("id") UUID id) { return binService.findById(id); }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public Response create(@PathParam("locationId") UUID locationId, @Valid CreateStorageBinRequest req) {
        return Response.status(Response.Status.CREATED).entity(binService.create(locationId, req)).build();
    }

    @DELETE @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) { binService.delete(id); return Response.noContent().build(); }
}
