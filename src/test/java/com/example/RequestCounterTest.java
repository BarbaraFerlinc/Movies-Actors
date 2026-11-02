package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class RequestCounterTest {

    private long total() {
        return given().get("/api/stats/requests").then().statusCode(200).extract().jsonPath().getLong("total");
    }

    @Test
    void everyRestCallIsCounted() {
        long before = total();
        given().get("/api/movies/all").then().statusCode(200);
        given().get("/api/actors/all").then().statusCode(200);
        assertEquals(before + 3, total());
    }

    @Test
    void breakdownByMethodIsExposed() {
        given().get("/api/movies/all");
        given().get("/api/stats/requests").then().body("byMethod.GET", greaterThanOrEqualTo(1));
    }
}
