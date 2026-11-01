package com.example.movies.dto;

import com.example.movies.domain.Actor;
import com.example.movies.domain.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public record ActorDto(
        Long id,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Past LocalDate birthDate,
        Set<String> pictures,
        Set<String> movieIds) {

    public static ActorDto from(Actor a) {
        Set<String> movieIds = new TreeSet<>();
        for (Movie m : a.movies) {
            movieIds.add(m.imdbId);
        }
        return new ActorDto(a.id, a.firstName, a.lastName, a.birthDate,
                new TreeSet<>(a.pictures), movieIds);
    }
}
