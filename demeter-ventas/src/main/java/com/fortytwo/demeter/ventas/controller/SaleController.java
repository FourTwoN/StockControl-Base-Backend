package com.fortytwo.demeter.ventas.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.ventas.dto.*;
import com.fortytwo.demeter.ventas.model.SaleStatus;
import com.fortytwo.demeter.ventas.service.SaleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SaleController {

    @Inject
    SaleService saleService;

    @GET
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("status") String status,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {

        if (status != null) {
            SaleStatus saleStatus = SaleStatus.valueOf(status);
            List<SaleDTO> sales = saleService.findByStatus(saleStatus);
            return Response.ok(sales).build();
        }

        if (from != null && to != null) {
            Instant fromInstant = Instant.parse(from);
            Instant toInstant = Instant.parse(to);
            List<SaleDTO> sales = saleService.findByDateRange(fromInstant, toInstant);
            return Response.ok(sales).build();
        }

        PagedResponse<SaleDTO> pagedResponse = saleService.findAll(page, size);
        return Response.ok(pagedResponse).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public SaleDTO getById(@PathParam("id") UUID id) {
        return saleService.findById(id);
    }

    @POST
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER})
    public Response create(@Valid CreateSaleRequest request) {
        SaleDTO created = saleService.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public SaleDTO update(@PathParam("id") UUID id, @Valid UpdateSaleRequest request) {
        return saleService.update(id, request);
    }

    @POST
    @Path("/{id}/complete")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public SaleDTO completeSale(@PathParam("id") UUID id) {
        return saleService.completeSale(id);
    }

    @POST
    @Path("/{id}/cancel")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR})
    public SaleDTO cancelSale(@PathParam("id") UUID id) {
        return saleService.cancelSale(id);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN})
    public Response delete(@PathParam("id") UUID id) {
        saleService.delete(id);
        return Response.noContent().build();
    }
}
