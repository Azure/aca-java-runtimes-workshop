package io.containerapps.javaruntime.workshop.quarkus.springboot;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static java.lang.System.Logger.Level.INFO;

@RestController("/springboot")
public class SpringbootResource {

    private final System.Logger LOGGER = System.getLogger(this.getClass().getName());

    private final StatisticsRepository repository;

    public SpringbootResource(StatisticsRepository statisticsRepository) {
        this.repository = statisticsRepository;
    }

    /**
     * Says hello.
     * <code>curl 'localhost:8701/springboot'</code>
     *
     * @return hello
     */
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        LOGGER.log(INFO, "SpringBoot: hello");
        return "SpringBoot: hello";
    }

    /**
     * Simulates requests that use a lot of CPU.
     * <code>curl 'localhost:8701/springboot/cpu?iterations=10&db=true'</code>
     *
     * @param iterations the number of iterations to run (times 20,000).
     * @return the result
     */
    @GetMapping(path = "/cpu", produces = MediaType.TEXT_PLAIN_VALUE)
    public String cpu(@RequestParam("iterations") Long iterations,
                      @RequestParam(value = "db", defaultValue = "false") Boolean db) {
        LOGGER.log(INFO, "SpringBoot: cpu: {0} {1}", iterations, db);

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

        String msg = "SpringBoot: CPU consumption is done with " + iterations + " iterations in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;

    }

    /**
     * Simulates requests that use a lot of memory.
     * <code>curl 'localhost:8701/springboot/memory?bites=10&db=true'</code>
     *
     * @param bites the number of megabytes to eat
     * @return the result.
     */
    @GetMapping(path = "/memory", produces = MediaType.TEXT_PLAIN_VALUE)
    public String memory(@RequestParam("bites") Integer bites,
                         @RequestParam(value = "db", defaultValue = "false") Boolean db) {
        LOGGER.log(INFO, "SpringBoot: memory: {0} {1}", bites, db);

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

        String msg = "SpringBoot: Memory consumption is done with " + bites + " bites in " + Duration.between(start, Instant.now()).getNano() + " nano-seconds.";
        if (db) {
            msg += " The result is persisted in the database.";
        }
        return msg;
    }
}
