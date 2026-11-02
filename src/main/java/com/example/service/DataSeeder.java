package com.example.service;

import com.example.domain.Actor;
import com.example.domain.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Transactional
public class DataSeeder {

    public record SeedResult(long actors, long movies) {}

    private static String picture(String seed) {
        return "https://picsum.photos/seed/" + seed + "/300/450";
    }

    public SeedResult seed() {
        if (Actor.count() > 0 || Movie.count() > 0) {
            throw new ClientErrorException(
                    "Database is not empty, seeding skipped", Response.Status.CONFLICT);
        }

        Map<String, Actor> actors = new HashMap<>();
        actor(actors, "Keanu", "Reeves", "1964-09-02");
        actor(actors, "Carrie-Anne", "Moss", "1967-08-21");
        actor(actors, "Laurence", "Fishburne", "1961-07-30");
        actor(actors, "Leonardo", "DiCaprio", "1974-11-11");
        actor(actors, "Kate", "Winslet", "1975-10-05");
        actor(actors, "Christian", "Bale", "1974-01-30");
        actor(actors, "Heath", "Ledger", "1979-04-04");
        actor(actors, "Tom", "Hanks", "1956-07-09");
        actor(actors, "Samuel L.", "Jackson", "1948-12-21");
        actor(actors, "John", "Travolta", "1954-02-18");
        actor(actors, "Brad", "Pitt", "1963-12-18");
        actor(actors, "Edward", "Norton", "1969-08-18");
        actor(actors, "Morgan", "Freeman", "1937-06-01");
        actor(actors, "Tim", "Robbins", "1958-10-16");

        movie(actors, "tt0133093", "The Matrix", 1999,
                "A hacker discovers that reality is a simulation and joins a rebellion against its controllers.",
                "Reeves", "Moss", "Fishburne");
        movie(actors, "tt1375666", "Inception", 2010,
                "A thief who steals secrets through dreams is given the task of planting an idea instead.",
                "DiCaprio");
        movie(actors, "tt0120338", "Titanic", 1997,
                "A young couple from different social classes fall in love aboard the ill-fated ship.",
                "DiCaprio", "Winslet");
        movie(actors, "tt0468569", "The Dark Knight", 2008,
                "Batman faces the Joker, a criminal mastermind who pushes Gotham into chaos.",
                "Bale", "Ledger");
        movie(actors, "tt0109830", "Forrest Gump", 1994,
                "A kind-hearted man from Alabama unknowingly takes part in decades of American history.",
                "Hanks");
        movie(actors, "tt0162222", "Cast Away", 2000,
                "A man survives a plane crash and spends years alone on a deserted island.",
                "Hanks");
        movie(actors, "tt0110912", "Pulp Fiction", 1994,
                "Intertwined stories of two hitmen, a boxer and a gangster's wife in Los Angeles.",
                "Jackson", "Travolta");
        movie(actors, "tt0137523", "Fight Club", 1999,
                "An insomniac office worker and a soap salesman start an underground fight club.",
                "Pitt", "Norton");
        movie(actors, "tt0111161", "The Shawshank Redemption", 1994,
                "Two imprisoned men bond over the years and find solace and eventual redemption.",
                "Freeman", "Robbins");
        movie(actors, "tt0114369", "Se7en", 1995,
                "Two detectives hunt a serial killer who uses the seven deadly sins as his motives.",
                "Pitt", "Freeman");

        return new SeedResult(Actor.count(), Movie.count());
    }

    private void actor(Map<String, Actor> actors, String firstName, String lastName, String birthDate) {
        Actor actor = new Actor();
        actor.firstName = firstName;
        actor.lastName = lastName;
        actor.birthDate = LocalDate.parse(birthDate);
        actor.pictures.add(picture("actor-" + lastName.toLowerCase()));

        actor.persist();
        actors.put(lastName, actor); // keyed by last name; fine because the sample names are unique
    }

    private void movie(Map<String, Actor> actors, String imdbId, String title, int year,
                       String description, String... cast) {
        Movie movie = new Movie();
        movie.imdbId = imdbId;
        movie.title = title;
        movie.year = year;
        movie.description = description;
        for (String lastName : cast) {
            movie.actors.add(actors.get(lastName));
        }
        movie.pictures.add(picture(imdbId));
        movie.pictures.add(picture(imdbId + "-2"));

        movie.persist();
    }

    public SeedResult clear() {
        long movies = Movie.count();
        long actors = Actor.count();

        Movie.<Movie>listAll().forEach(Movie::delete);
        Movie.flush();
        Actor.<Actor>listAll().forEach(Actor::delete);
        Actor.flush();

        return new SeedResult(actors, movies);
    }
}