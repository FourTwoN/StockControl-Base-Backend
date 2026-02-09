package com.fortytwo.demeter.fotos.controller;

import com.fortytwo.demeter.common.auth.RoleConstants;
import com.fortytwo.demeter.fotos.dto.ClassificationDTO;
import com.fortytwo.demeter.fotos.dto.DetectionDTO;
import com.fortytwo.demeter.fotos.dto.S3ImageDTO;
import com.fortytwo.demeter.fotos.service.ImageService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImageController {

    @Inject
    ImageService imageService;

    @GET
    @Path("/{id}")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public S3ImageDTO getById(@PathParam("id") UUID id) {
        return imageService.findById(id);
    }

    @GET
    @Path("/{id}/detections")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<DetectionDTO> getDetections(@PathParam("id") UUID id) {
        return imageService.findDetectionsByImageId(id);
    }

    @GET
    @Path("/{id}/classifications")
    @RolesAllowed({RoleConstants.ADMIN, RoleConstants.SUPERVISOR, RoleConstants.WORKER, RoleConstants.VIEWER})
    public List<ClassificationDTO> getClassifications(@PathParam("id") UUID id) {
        return imageService.findClassificationsByImageId(id);
    }
}
