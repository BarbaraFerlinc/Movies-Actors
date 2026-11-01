package com.example.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Actor extends PanacheEntity {

    public String firstName;
    public String lastName;
    public LocalDate birthDate;

    @ElementCollection
    public Set<String> pictures = new HashSet<>();

    @ManyToMany(mappedBy = "actors")
    public Set<Movie> movies = new HashSet<>();

    @Version
    public long version;
}
