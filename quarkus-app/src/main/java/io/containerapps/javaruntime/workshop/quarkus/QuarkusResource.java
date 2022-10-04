package io.containerapps.javaruntime.workshop.quarkus;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.lang.System.Logger;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static java.lang.System.Logger.Level.INFO;

@Path("/quarkus")
@Produces(MediaType.TEXT_PLAIN)
public class QuarkusResource {

    private final Logger LOGGER = System.getLogger(this.getClass().getName());

    private final StatisticsRepository repository;

    public QuarkusResource(StatisticsRepository statisticsRepository) {
        this.repository = statisticsRepository;
    }

    /**
     * Says hello.
     * <code>curl 'localhost:8701/quarkus'</code>
     *
     * @return hello
     */
    @GET
    public String hello() {
        LOGGER.log(INFO, "Quarkus: hello");
        return "Quarkus: hello";
    }

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
    @GET
    @Path("/cpu")
    public String cpu(@QueryParam("iterations") @DefaultValue("10") Long iterations,
                      @QueryParam("db") @DefaultValue("false") Boolean db,
                      @QueryParam("desc") String desc) {
        LOGGER.log(INFO, "Quarkus: cpu: {0} {1} with desc {2}", iterations, db, desc);
        Long iterationsDone = iterations;

        // tag::adocAlgoCPU[]
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
}
