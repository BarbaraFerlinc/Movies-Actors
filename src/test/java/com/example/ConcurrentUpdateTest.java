package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ConcurrentUpdateTest {

    @Test
    void parallelUpdatesOfSameActorNeverCauseServerErrors() throws Exception {
        int actorId = given().contentType(JSON)
                .body(Map.of("firstName", "Start", "lastName", "Concurrent"))
                .post("/api/actors")
                .then().statusCode(201)
                .extract().path("id");

        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Integer>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            String name = "Writer" + i;
            results.add(pool.submit(() -> {
                ready.countDown();
                go.await(); // all threads fire at the same moment
                return given().contentType(JSON)
                        .body(Map.of("firstName", name, "lastName", "Concurrent"))
                        .put("/api/actors/{id}", actorId)
                        .statusCode();
            }));
        }

        ready.await();
        go.countDown();

        boolean anySuccess = false;
        for (Future<Integer> result : results) {
            int status = result.get();
            assertTrue(status == 200 || status == 409, "unexpected status " + status);
            anySuccess |= status == 200;
        }
        pool.shutdown();

        assertTrue(anySuccess, "at least one update must succeed");
        given().get("/api/actors/{id}", actorId)
                .then().statusCode(200)
                .body("firstName", startsWith("Writer"));
    }
}