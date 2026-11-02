package com.example.api;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Singleton
@Path("/api/stats/requests")
@Produces(MediaType.APPLICATION_JSON)
public class RequestStatsResource {

    public record RequestStats(long total, Map<String, Long> byMethod) {}

    private final LongAdder total = new LongAdder();
    private final Map<String, LongAdder> byMethod = new ConcurrentHashMap<>();

    @ServerRequestFilter
    public void count(ContainerRequestContext ctx) {
        if (ctx.getUriInfo().getPath().startsWith("/api")) {
            total.increment();
            byMethod.computeIfAbsent(ctx.getMethod(), k -> new LongAdder()).increment();
        }
    }

    @GET
    public RequestStats stats() {
        Map<String, Long> snapshot = new TreeMap<>();
        byMethod.forEach((method, count) -> snapshot.put(method, count.sum()));
        return new RequestStats(total.sum(), snapshot);
    }
}