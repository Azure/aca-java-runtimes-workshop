package io.containerapps.javaruntime.workshop.springboot;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
public class Statistics{

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statistics_generator")
    @SequenceGenerator(name="statistics_generator", sequenceName = "statistics_seq")
    @Id
    private Long id;
    @Column(name = "done_at")
    public Instant doneAt = Instant.now();
    public Framework framework = Framework.SPRINGBOOT;
    public Type type;
    public String parameter;
    @Column(name = "duration", columnDefinition = "NUMERIC", length = 21)
    public Duration duration;
    public String description;
}

enum Type {
    CPU, MEMORY
}

enum Framework {
    QUARKUS, MICRONAUT, SPRINGBOOT
}
