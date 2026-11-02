package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class MovieResourceTest {

    private Map<String, Object> movie(String imdbId, String title, List<Integer> actorIds) {
        return Map.of("imdbId", imdbId, "title", title, "year", 1999,
                "description", "A test movie", "pictures", List.of("https://example.com/p.jpg"),
                "actorIds", actorIds);
    }

    @Test
    void crudLifecycleWithActor() {
        int actorId = given().contentType(JSON)
                .body(Map.of("firstName", "Carrie-Anne", "lastName", "Moss"))
                .post("/api/actors").then().statusCode(201).extract().path("id");

        given().contentType(JSON).body(movie("tt0133093", "The Matrix", List.of(actorId)))
                .post("/api/movies")
                .then().statusCode(201)
                .header("Location", endsWith("/api/movies/tt0133093"))
                .body("actorIds", hasItem(actorId));

        given().get("/api/movies/tt0133093")
                .then().statusCode(200)
                .body("title", is("The Matrix"), "pictures", hasSize(1));

        given().get("/api/actors/{id}", actorId)
                .then().body("movieIds", hasItem("tt0133093"));

        given().contentType(JSON).body(movie("tt0133093", "The Matrix (Remastered)", List.of()))
                .put("/api/movies/tt0133093")
                .then().statusCode(200)
                .body("title", is("The Matrix (Remastered)"), "actorIds", empty());

        given().delete("/api/movies/tt0133093").then().statusCode(204);
        given().get("/api/movies/tt0133093").then().statusCode(404);
    }

    @Test
    void duplicateImdbIdConflict() {
        given().contentType(JSON).body(movie("tt0000001", "Dup", List.of())).post("/api/movies").then().statusCode(201);
        given().contentType(JSON).body(movie("tt0000001", "Dup", List.of())).post("/api/movies").then().statusCode(409);
    }

    @Test
    void rejectInvalidMovie() {
        given().contentType(JSON).body(movie("not-an-id", "Bad", List.of()))
                .post("/api/movies").then().statusCode(400);
        given().contentType(JSON).body(movie("tt0000002", "Ghost cast", List.of(987654)))
                .post("/api/movies").then().statusCode(400);
    }

    @Test
    void paginationAndSearch() {
        for (int i = 1; i <= 3; i++) {
            given().contentType(JSON).body(movie("tt200000" + i, "Searchable Saga " + i, List.of()))
                    .post("/api/movies").then().statusCode(201);
        }
        given().queryParam("q", "searchable saga").queryParam("size", 2)
                .get("/api/movies")
                .then().statusCode(200)
                .body("items", hasSize(2), "totalItems", is(3), "totalPages", is(2),
                        "items[0].title", is("Searchable Saga 1"));

        given().get("/api/movies/all").then().statusCode(200).body("size()", greaterThanOrEqualTo(3));
    }
}
