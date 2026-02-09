package com.fortytwo.demeter.ubicaciones.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.service.StorageAreaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/warehouses/{warehouseId}/areas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageAreaController {
    @Inject StorageAreaService areaService;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<StorageAreaDTO> list(@PathParam("warehouseId") UUID warehouseId) { return areaService.findByWarehouse(warehouseId); }

    @GET @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public StorageAreaDTO getById(@PathParam("id") UUID id) { return areaService.findById(id); }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public Response create(@PathParam("warehouseId") UUID warehouseId, @Valid CreateStorageAreaRequest req) {
        return Response.status(Response.Status.CREATED).entity(areaService.create(warehouseId, req)).build();
    }

    @PUT @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public StorageAreaDTO update(@PathParam("id") UUID id, @Valid CreateStorageAreaRequest req) { return areaService.update(id, req); }

    @DELETE @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) { areaService.delete(id); return Response.noContent().build(); }
}
