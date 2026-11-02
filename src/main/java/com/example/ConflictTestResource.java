package com.example;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test/conflict")
public class ConflictTestResource {

    @GET
    public String conflict() {
        throw new OptimisticLockException("simulated conflict");
    }
}