package com.fortytwo.demeter.ubicaciones.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.service.StorageLocationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/areas/{areaId}/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageLocationController {
    @Inject StorageLocationService locationService;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<StorageLocationDTO> list(@PathParam("areaId") UUID areaId) { return locationService.findByArea(areaId); }

    @GET @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public StorageLocationDTO getById(@PathParam("id") UUID id) { return locationService.findById(id); }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public Response create(@PathParam("areaId") UUID areaId, @Valid CreateStorageLocationRequest req) {
        return Response.status(Response.Status.CREATED).entity(locationService.create(areaId, req)).build();
    }

    @PUT @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public StorageLocationDTO update(@PathParam("id") UUID id, @Valid CreateStorageLocationRequest req) { return locationService.update(id, req); }

    @DELETE @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) { locationService.delete(id); return Response.noContent().build(); }
}
