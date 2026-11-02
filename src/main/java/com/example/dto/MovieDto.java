package com.example.dto;

import com.example.domain.Actor;
import com.example.domain.Movie;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import java.util.Set;
import java.util.TreeSet;

public record MovieDto(
        @NotBlank @Pattern(regexp = "^tt\\d{7,10}$", message = "must be an IMDb id such as tt0133093") String imdbId,
        @NotBlank @Size(max = 255) String title,
        @NotNull @Min(1888) @Max(2200) Integer year,
        Set<String> genres,
        @Positive Integer runtimeMinutes,
        @Size(max = 2000) String description,
        Set<String> pictures,
        Set<Long> actorIds) {

    public static MovieDto from(Movie m) {
        Set<Long> actorIds = new TreeSet<>();
        for (Actor a : m.actors) {
            actorIds.add(a.id);
        }
        return new MovieDto(m.imdbId, m.title, m.year,
                new TreeSet<>(m.genres), m.runtimeMinutes,
                m.description, new TreeSet<>(m.pictures), actorIds);
    }
}
