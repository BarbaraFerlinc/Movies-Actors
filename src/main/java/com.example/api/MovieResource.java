package com.example.api;

import com.example.dto.MovieDto;
import com.example.dto.PageResponse;
import com.example.service.MovieService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/api/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieService service;

    @GET
    @Path("/all")
    public List<MovieDto> all() {
        return service.listAll();
    }

    @GET
    public PageResponse<MovieDto> list(@QueryParam("q") String q,
                                       @QueryParam("page") @DefaultValue("0") @Min(0) int page,
                                       @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return service.list(q, page, size);
    }

    @GET
    @Path("/{imdbId}")
    public MovieDto get(@PathParam("imdbId") String imdbId) {
        return service.get(imdbId);
    }

    @POST
    public Response create(@Valid MovieDto dto) {
        MovieDto created = service.create(dto);
        return Response.created(URI.create("/api/movies/" + created.imdbId())).entity(created).build();
    }

    @PUT
    @Path("/{imdbId}")
    public MovieDto update(@PathParam("imdbId") String imdbId, @Valid MovieDto dto) {
        return service.update(imdbId, dto);
    }

    @DELETE
    @Path("/{imdbId}")
    public Response delete(@PathParam("imdbId") String imdbId) {
        service.delete(imdbId);
        return Response.noContent().build();
    }
}
