package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class ActorResourceTest {

    private int createActor(String first, String last) {
        return given().contentType(JSON)
                .body(Map.of("firstName", first, "lastName", last, "birthDate", "1964-09-02"))
                .post("/api/actors")
                .then().statusCode(201)
                .header("Location", containsString("/api/actors/"))
                .extract().path("id");
    }

    @Test
    void crudLifecycle() {
        int id = createActor("Keanu", "Reeves");

        given().get("/api/actors/{id}", id)
                .then().statusCode(200)
                .body("firstName", is("Keanu"), "birthDate", is("1964-09-02"));

        given().contentType(JSON)
                .body(Map.of("firstName", "Keanu Charles", "lastName", "Reeves"))
                .put("/api/actors/{id}", id)
                .then().statusCode(200).body("firstName", is("Keanu Charles"));

        given().delete("/api/actors/{id}", id).then().statusCode(204);
        given().get("/api/actors/{id}", id).then().statusCode(404);
    }

    @Test
    void rejectBlankNameAndFutureBirthDate() {
        given().contentType(JSON)
                .body(Map.of("firstName", "", "lastName", "X", "birthDate", "2999-01-01"))
                .post("/api/actors")
                .then().statusCode(400);
    }

    @Test
    void picturesArePersistedAndUpdated() {
        int id = given().contentType(JSON)
                .body(Map.of("firstName", "Pic", "lastName", "Tester",
                        "pictures", List.of("https://example.com/a.jpg", "https://example.com/b.jpg")))
                .post("/api/actors")
                .then().statusCode(201)
                .body("pictures", hasSize(2))
                .extract().path("id");

        given().get("/api/actors/{id}", id)
                .then().body("pictures", hasItems("https://example.com/a.jpg", "https://example.com/b.jpg"));

        given().contentType(JSON)
                .body(Map.of("firstName", "Pic", "lastName", "Tester", "pictures", List.of()))
                .put("/api/actors/{id}", id)
                .then().statusCode(200).body("pictures", empty());
    }

    @Test
    void paginationAndSearch() {
        for (int i = 1; i <= 3; i++) {
            createActor("Pager" + i, "Paginated");
        }
        given().queryParam("q", "paginated").queryParam("size", 2).queryParam("page", 0)
                .get("/api/actors")
                .then().statusCode(200)
                .body("items", hasSize(2), "totalItems", is(3), "totalPages", is(2));

        given().queryParam("q", "paginated").queryParam("size", 2).queryParam("page", 1)
                .get("/api/actors")
                .then().body("items", hasSize(1));
    }

    @Test
    void invalidPageSizeIsRejected() {
        given().queryParam("size", 0).get("/api/actors").then().statusCode(400);
    }
}
