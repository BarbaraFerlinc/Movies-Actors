package com.example.service;

import com.example.domain.Actor;
import com.example.domain.Movie;
import com.example.dto.MovieDto;
import com.example.dto.PageResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Transactional
public class MovieService {

    private static final Sort ORDER = Sort.by("title");

    public List<MovieDto> listAll() {
        return Movie.<Movie>findAll(ORDER).stream().map(MovieDto::from).toList();
    }

    public PageResponse<MovieDto> list(String q, int page, int size) {
        PanacheQuery<Movie> query = (q == null || q.isBlank())
                ? Movie.findAll(ORDER)
                : Movie.find("lower(title) like ?1 or lower(description) like ?1", ORDER,
                "%" + q.trim().toLowerCase() + "%");
        List<MovieDto> items = query.page(Page.of(page, size)).stream().map(MovieDto::from).toList();
        return PageResponse.of(items, page, size, query.count());
    }

    public MovieDto get(String imdbId) {
        return MovieDto.from(find(imdbId));
    }

    public MovieDto create(MovieDto dto) {
        if (Movie.findById(dto.imdbId()) != null) {
            throw new ClientErrorException("Movie " + dto.imdbId() + " already exists", Response.Status.CONFLICT);
        }
        Movie movie = new Movie();
        movie.imdbId = dto.imdbId();
        apply(movie, dto);
        movie.persist();
        return MovieDto.from(movie);
    }

    public MovieDto update(String imdbId, MovieDto dto) {
        Movie movie = find(imdbId);
        apply(movie, dto);
        movie.flush();
        return MovieDto.from(movie);
    }

    public void delete(String imdbId) {
        find(imdbId).delete();
    }

    private Movie find(String imdbId) {
        Movie movie = Movie.findById(imdbId);
        if (movie == null) {
            throw new NotFoundException("Movie " + imdbId + " not found");
        }
        return movie;
    }

    private void apply(Movie movie, MovieDto dto) {
        movie.title = dto.title();
        movie.year = dto.year();
        movie.genres.clear();
        if (dto.genres() != null) {
            movie.genres.addAll(dto.genres());
        }
        movie.runtimeMinutes = dto.runtimeMinutes();
        movie.description = dto.description();
        movie.pictures.clear();
        if (dto.pictures() != null) {
            movie.pictures.addAll(dto.pictures());
        }
        movie.actors.clear();
        movie.actors.addAll(resolveActors(dto.actorIds()));
    }

    private Set<Actor> resolveActors(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        List<Actor> found = Actor.list("id in ?1", ids);
        if (found.size() != ids.size()) {
            throw new BadRequestException("One or more actor ids do not exist");
        }
        return new HashSet<>(found);
    }
}
