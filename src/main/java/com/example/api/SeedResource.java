package com.example.api;

import com.example.service.DataSeeder;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
public class SeedResource {

    @Inject
    DataSeeder seeder;

    @POST
    @Path("/seed")
    public Response seed() {
        return Response.status(Response.Status.CREATED).entity(seeder.seed()).build();
    }

    @DELETE
    @Path("/data")
    public DataSeeder.SeedResult clear(@QueryParam("confirm") boolean confirm) {
        if (!confirm) {
            throw new BadRequestException("This deletes ALL movies and actors. Add ?confirm=true to proceed");
        }
        return seeder.clear();
    }
}