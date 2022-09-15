package io.containerapps.javaruntime.workshop.quarkus;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

@Path("/quarkus")
@Produces(MediaType.TEXT_PLAIN)
@Transactional
public class QuarkusResource {

    @GET
    public String hello() {
        return "Hello from Quarkus";
    }

    /**
     * Simulates requests that use a lot of CPU.
     * <code>curl 'localhost:8701/quarkus/cpu?iterations=10&db=true'</code>
     *
     * @param iterations the number of iterations to run (times 20,000).
     * @return the result
     */
    @GET
    @Path("/cpu")
    public String cpu(@QueryParam("iterations") Long iterations,
                      @QueryParam("db") @DefaultValue("false") Boolean db) {

        Instant start = Instant.now();
        if (iterations == null) {
            iterations = 20000L;
        } else {
            iterations *= 20000;
        }
        while (iterations > 0) {
            if (iterations % 20000 == 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ie) {
                }
            }
            iterations--;
        }

        if (db) {
            Statistics statistics = new Statistics();
            statistics.type = Type.CPU;
            statistics.parameter = iterations.toString();
            statistics.duration = Duration.between(start, Instant.now());
            statistics.persist();
        }

        String msg = "CPU consumption is done with " + iterations + " iterations in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;

    }

    /**
     * Simulates requests that use a lot of memory.
     * <code>curl 'localhost:8701/quarkus/memory?bites=10&db=true'</code>
     *
     * @param bites the number of megabytes to eat
     * @return the result.
     */
    @GET
    @Path("/memory")
    public String memory(@QueryParam("bites") Integer bites,
                         @QueryParam("db") @DefaultValue("false") Boolean db) {

        Instant start = Instant.now();
        if (bites == null) {
            bites = 1;
        }
        HashMap hunger = new HashMap<>();
        for (int i = 0; i < bites * 1024 * 1024; i += 8192) {
            byte[] bytes = new byte[8192];
            hunger.put(i, bytes);
            for (int j = 0; j < 8192; j++) {
                bytes[j] = '0';
            }
        }

        if (db) {
            Statistics statistics = new Statistics();
            statistics.type = Type.MEMORY;
            statistics.parameter = bites.toString();
            statistics.duration = Duration.between(start, Instant.now());
            statistics.persist();
        }

        String msg = "Memory consumption is done with " + bites + " bites in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;
    }
}
