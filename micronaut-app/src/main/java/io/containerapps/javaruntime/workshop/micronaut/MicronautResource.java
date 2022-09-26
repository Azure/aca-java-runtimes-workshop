package io.containerapps.javaruntime.workshop.micronaut;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static java.lang.System.Logger.Level.INFO;

@Controller("/micronaut")
public class MicronautResource {

    private final System.Logger LOGGER = System.getLogger(this.getClass().getName());

    private final StatisticsRepository repository;

    public MicronautResource(StatisticsRepository statisticsRepository) {
        this.repository = statisticsRepository;
    }

    /**
     * Says hello.
     * <code>curl 'localhost:8702/micronaut'</code>
     *
     * @return hello
     */
    @Get(produces = MediaType.TEXT_PLAIN)
    public String hello() {
        LOGGER.log(INFO, "Micronaut: hello");
        return "Micronaut: hello";
    }

    /**
     * Simulates requests that use a lot of CPU.
     * <code>curl 'localhost:8702/micronaut/cpu'</code>
     * <code>curl 'localhost:8702/micronaut/cpu?iterations=10'</code>
     * <code>curl 'localhost:8702/micronaut/cpu?iterations=10&db=true'</code>
     *
     * @param iterations the number of iterations to run (times 20,000).
     * @return the result
     */
    @Get(uri = "/cpu", produces = MediaType.TEXT_PLAIN)
    public String cpu(@QueryValue(value = "iterations", defaultValue = "10") Long iterations,
                      @QueryValue(value = "db", defaultValue = "false") Boolean db) {
        LOGGER.log(INFO, "Micronaut: cpu: {0} {1}", iterations, db);
        Long iterationsDone = iterations;

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
            repository.save(statistics);
        }

        String msg = "Micronaut: CPU consumption is done with " + iterationsDone + " iterations in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;

    }

    /**
     * Simulates requests that use a lot of memory.
     * <code>curl 'localhost:8702/micronaut/memory'</code>
     * <code>curl 'localhost:8702/micronaut/memory?bites=10'</code>
     * <code>curl 'localhost:8702/micronaut/memory?bites=10&db=true'</code>
     *
     * @param bites the number of megabytes to eat
     * @return the result.
     */
    @Get(uri = "/memory", produces = MediaType.TEXT_PLAIN)
    public String memory(@QueryValue(value = "bites", defaultValue = "false") Integer bites,
                         @QueryValue(value = "db", defaultValue = "false") Boolean db) {
        LOGGER.log(INFO, "Micronaut: memory: {0} {1}", bites, db);

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
            repository.save(statistics);
        }

        String msg = "Micronaut: Memory consumption is done with " + bites + " bites in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;
    }
}
