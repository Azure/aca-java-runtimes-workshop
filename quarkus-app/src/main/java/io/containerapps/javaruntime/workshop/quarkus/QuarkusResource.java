// tag::adocHeader[]
package io.containerapps.javaruntime.workshop.quarkus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.System.Logger;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.Logger.Level.INFO;

@Path("/quarkus")
@Produces(MediaType.TEXT_PLAIN)
public class QuarkusResource {

    private final Logger LOGGER = System.getLogger(this.getClass().getName());

    private final StatisticsRepository repository;

    public QuarkusResource(StatisticsRepository statisticsRepository) {
        this.repository = statisticsRepository;
    }
// end::adocHeader[]

    /**
     * Says hello.
     * <code>curl 'localhost:8701/quarkus'</code>
     *
     * @return hello
     */
// tag::adocMethodHello[]
    @GET
    public String hello() {
        LOGGER.log(INFO, "Quarkus: hello");
        return "Quarkus: hello";
    }
// end::adocMethodHello[]

    /**
     * Simulates requests that use a lot of CPU.
     * <code>curl 'localhost:8701/quarkus/cpu'</code>
     * <code>curl 'localhost:8701/quarkus/cpu?iterations=10'</code>
     * <code>curl 'localhost:8701/quarkus/cpu?iterations=10&db=true'</code>
     * <code>curl 'localhost:8701/quarkus/cpu?iterations=10&db=true&desc=java17'</code>
     *
     * @param iterations the number of iterations to run (times 20,000).
     * @return the result
     */
// tag::adocMethodCPU[]
    @GET
    @Path("/cpu")
    public String cpu(@QueryParam("iterations") @DefaultValue("10") Long iterations,
                      @QueryParam("db") @DefaultValue("false") Boolean db,
                      @QueryParam("desc") String desc) {
        LOGGER.log(INFO, "Quarkus: cpu: {0} {1} with desc {2}", iterations, db, desc);
        Long iterationsDone = iterations;

        Instant start = Instant.now();
        if (iterations == null) {
            iterations = 20000L;
        } else {
            iterations *= 20000;
        }
        // tag::adocAlgoCPU[]
        while (iterations > 0) {
            if (iterations % 20000 == 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ie) {
                }
            }
            iterations--;
        }
        // end::adocAlgoCPU[]

        if (db) {
            Statistics statistics = new Statistics();
            statistics.type = Type.CPU;
            statistics.parameter = iterations.toString();
            statistics.duration = Duration.between(start, Instant.now());
            statistics.description = desc;
            repository.persist(statistics);
        }

        String msg = "Quarkus: CPU consumption is done with " + iterationsDone + " iterations in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;
    }
// end::adocMethodCPU[]

    /**
     * Simulates requests that use a lot of memory.
     * <code>curl 'localhost:8701/quarkus/memory'</code>
     * <code>curl 'localhost:8701/quarkus/memory?bites=10'</code>
     * <code>curl 'localhost:8701/quarkus/memory?bites=10&db=true'</code>
     * <code>curl 'localhost:8701/quarkus/memory?bites=10&db=true&desc=java17'</code>
     *
     * @param bites the number of megabytes to eat
     * @return the result.
     */
// tag::adocMethodMemory[]
    @GET
    @Path("/memory")
    public String memory(@QueryParam("bites") @DefaultValue("10") Integer bites,
                         @QueryParam("db") @DefaultValue("false") Boolean db,
                         @QueryParam("desc") String desc) {
        LOGGER.log(INFO, "Quarkus: memory: {0} {1} with desc {2}", bites, db, desc);

        Instant start = Instant.now();
        if (bites == null) {
            bites = 1;
        }
        // tag::adocAlgoMemory[]
        HashMap hunger = new HashMap<>();
        for (int i = 0; i < bites * 1024 * 1024; i += 8192) {
            byte[] bytes = new byte[8192];
            hunger.put(i, bytes);
            for (int j = 0; j < 8192; j++) {
                bytes[j] = '0';
            }
        }
        // end::adocAlgoMemory[]

        if (db) {
            Statistics statistics = new Statistics();
            statistics.type = Type.MEMORY;
            statistics.parameter = bites.toString();
            statistics.duration = Duration.between(start, Instant.now());
            statistics.description = desc;
            repository.persist(statistics);
        }

        String msg = "Quarkus: Memory consumption is done with " + bites + " bites in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;
    }
// end::adocMethodMemory[]

// tag::adocMethodStats[]
    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Statistics> stats() {
        LOGGER.log(INFO, "Quarkus: retrieving statistics");
        return Statistics.findAll().list();
    }
// end::adocMethodStats[]
}
