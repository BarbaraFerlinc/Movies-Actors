package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class ConflictExceptionMapperTest {

    @Test
    void optimisticLockExceptionIsReturnedAs409() {
        given().get("/test/conflict")
                .then().statusCode(409)
                .contentType("application/json")
                .body("error", containsString("modified by another client"));
    }
}