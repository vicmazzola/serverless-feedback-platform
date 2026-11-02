package br.com.fiap.feedback.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EvaluationResource {
    @Inject
    FeedbackService service;

    @POST
    public Response create(EvaluationRequest request) {
        try {
            return Response.status(Response.Status.CREATED).entity(EvaluationResponse.from(service.create(request))).build();
        } catch (InvalidEvaluationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ApiError(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiError("Unable to save evaluation")).build();
        }
    }

    record ApiError(String message) {
    }
}
