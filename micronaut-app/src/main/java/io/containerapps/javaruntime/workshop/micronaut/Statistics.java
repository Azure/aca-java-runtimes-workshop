package io.containerapps.javaruntime.workshop.micronaut;

import javax.persistence.*;
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
    public Framework framework = Framework.MICRONAUT;
    public Type type;
    public String parameter;
    @Column(name = "duration", columnDefinition = "NUMERIC", length = 21)
    public Duration duration;
    public String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

enum Type {
    CPU, MEMORY
}

enum Framework {
    QUARKUS, MICRONAUT, SPRINGBOOT
}
