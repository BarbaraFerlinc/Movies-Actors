package com.example.service;

import com.example.domain.Actor;
import com.example.domain.Movie;
import com.example.dto.ActorDto;
import com.example.dto.PageResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
@Transactional
public class ActorService {

    private static final Sort ORDER = Sort.by("lastName").and("firstName");

    public List<ActorDto> listAll() {
        return Actor.<Actor>findAll(ORDER).stream().map(ActorDto::from).toList();
    }

    public PageResponse<ActorDto> list(String q, int page, int size) {
        PanacheQuery<Actor> query = (q == null || q.isBlank())
                ? Actor.findAll(ORDER)
                : Actor.find("lower(firstName) like ?1 or lower(lastName) like ?1", ORDER,
                "%" + q.trim().toLowerCase() + "%");
        List<ActorDto> items = query.page(Page.of(page, size)).stream().map(ActorDto::from).toList();
        return PageResponse.of(items, page, size, query.count());
    }

    public ActorDto get(Long id) {
        return ActorDto.from(find(id));
    }

    public ActorDto create(ActorDto dto) {
        Actor actor = new Actor();
        apply(actor, dto);
        actor.persist();
        return ActorDto.from(actor);
    }

    public ActorDto update(Long id, ActorDto dto) {
        Actor actor = find(id);
        apply(actor, dto);
        actor.flush();
        return ActorDto.from(actor);
    }

    public void delete(Long id) {
        Actor actor = find(id);
        for (Movie movie : actor.movies) {
            movie.actors.remove(actor);
        }
        actor.delete();
    }

    private Actor find(Long id) {
        Actor actor = Actor.findById(id);
        if (actor == null) {
            throw new NotFoundException("Actor " + id + " not found");
        }
        return actor;
    }

    private void apply(Actor actor, ActorDto dto) {
        actor.firstName = dto.firstName();
        actor.lastName = dto.lastName();
        actor.birthDate = dto.birthDate();
        actor.pictures.clear();
        if (dto.pictures() != null) {
            actor.pictures.addAll(dto.pictures());
        }
    }
}
