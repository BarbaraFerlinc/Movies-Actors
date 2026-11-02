package com.example.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Movie extends PanacheEntityBase {

    @Id
    public String imdbId;

    public String title;

    @Column(name = "release_year")
    public Integer year;

    @ElementCollection
    public Set<String> genres = new HashSet<>();

    public Integer runtimeMinutes;

    @Column(length = 2000)
    public String description;

    @ElementCollection
    public Set<String> pictures = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "movie_actor")
    public Set<Actor> actors = new HashSet<>();

    @Version
    public long version;
}
