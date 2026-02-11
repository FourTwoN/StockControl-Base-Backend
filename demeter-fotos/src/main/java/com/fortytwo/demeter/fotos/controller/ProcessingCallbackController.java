package com.fortytwo.demeter.fotos.controller;

import com.fortytwo.demeter.fotos.dto.ProcessingResultRequest;
import com.fortytwo.demeter.fotos.dto.SessionStatusDTO;
import com.fortytwo.demeter.fotos.service.ProcessingResultService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import java.util.UUID;

/**
 * Callback endpoint for ML Worker to report processing results.
 *
 * <p>This endpoint is called by the ML Worker Cloud Run service after
 * completing image processing. It receives detections, classifications,
 * and estimations and persists them to the database.
 *
 * <p>Authentication: The ML Worker authenticates via OIDC token issued
 * by Cloud Tasks. The X-Tenant-ID header is required for multi-tenant
 * isolation.
 */
@Path("/api/v1/processing-callback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Processing Callback", description = "ML Worker callback endpoints")
public class ProcessingCallbackController {

    private static final Logger log = Logger.getLogger(ProcessingCallbackController.class);

    @Inject
    ProcessingResultService processingResultService;

    @POST
    @Path("/results")
    @Operation(
            summary = "Receive ML processing results",
            description = "Called by ML Worker to report detections, classifications, and estimations"
    )
    @APIResponse(
            responseCode = "200",
            description = "Results processed successfully",
            content = @Content(schema = @Schema(implementation = SessionStatusDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid request payload")
    @APIResponse(responseCode = "404", description = "Session or image not found")
    public Response receiveResults(
            @HeaderParam("X-Tenant-ID") String tenantId,
            @Valid @RequestBody ProcessingResultRequest request
    ) {
        log.infof("Received processing results: session=%s, image=%s, tenant=%s",
                request.sessionId(), request.imageId(), tenantId);

        SessionStatusDTO status = processingResultService.processResults(request);

        return Response.ok(status).build();
    }

    @POST
    @Path("/error")
    @Operation(
            summary = "Report processing error",
            description = "Called by ML Worker when image processing fails"
    )
    @APIResponse(
            responseCode = "200",
            description = "Error recorded",
            content = @Content(schema = @Schema(implementation = SessionStatusDTO.class))
    )
    public Response reportError(
            @HeaderParam("X-Tenant-ID") String tenantId,
            ErrorReport errorReport
    ) {
        log.warnf("Processing error reported: session=%s, image=%s, error=%s",
                errorReport.sessionId(), errorReport.imageId(), errorReport.errorMessage());

        SessionStatusDTO status = processingResultService.markFailed(
                errorReport.sessionId(),
                errorReport.imageId(),
                errorReport.errorMessage()
        );

        return Response.ok(status).build();
    }

    /**
     * Error report from ML Worker.
     */
    public record ErrorReport(
            UUID sessionId,
            UUID imageId,
            String errorMessage,
            String errorType
    ) {}
}
