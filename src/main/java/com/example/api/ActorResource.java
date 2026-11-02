package com.example.api;

import com.example.dto.ActorDto;
import com.example.dto.PageResponse;
import com.example.service.ActorService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/api/actors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActorResource {

    @Inject
    ActorService service;

    @GET
    @Path("/all")
    public List<ActorDto> all() {
        return service.listAll();
    }

    @GET
    public PageResponse<ActorDto> list(@QueryParam("q") String q,
                                       @QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                       @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return service.list(q, page, size);
    }

    @GET
    @Path("/{id}")
    public ActorDto get(@PathParam("id") Long id) {
        return service.get(id);
    }

    @POST
    public Response create(@Valid ActorDto dto) {
        ActorDto created = service.create(dto);
        return Response.created(URI.create("/api/actors/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public ActorDto update(@PathParam("id") Long id, @Valid ActorDto dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
