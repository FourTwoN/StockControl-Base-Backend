package com.fortytwo.demeter.common.tenant;

import com.fortytwo.demeter.common.auth.RoleConstants;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantConfigController {

    @Inject
    TenantConfigService tenantConfigService;

    @GET
    @Path("/{tenantId}/config")
    @PermitAll
    public TenantConfigResponse getConfig(@PathParam("tenantId") String tenantId) {
        return tenantConfigService.findConfig(tenantId);
    }

    @GET
    @RolesAllowed({RoleConstants.ADMIN})
    public List<Tenant> list() {
        return tenantConfigService.findAll();
    }

    @POST
    @RolesAllowed({RoleConstants.ADMIN})
    public Response create(@Valid CreateTenantRequest request) {
        Tenant created = tenantConfigService.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{tenantId}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Tenant update(@PathParam("tenantId") String tenantId, @Valid UpdateTenantRequest request) {
        return tenantConfigService.update(tenantId, request);
    }

    @DELETE
    @Path("/{tenantId}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("tenantId") String tenantId) {
        tenantConfigService.delete(tenantId);
        return Response.noContent().build();
    }
}
