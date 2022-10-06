package io.containerapps.javaruntime.workshop.springboot;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "Statistics_Springboot")
public class Statistics{

    @GeneratedValue
    @Id
    private Long id;
    @Column(name = "done_at")
    public Instant doneAt = Instant.now();
    public Framework framework = Framework.SPRINGBOOT;
    public Type type;
    public String parameter;
    public Duration duration;
    public String description;
}

enum Type {
    CPU, MEMORY
}

enum Framework {
    QUARKUS, MICRONAUT, SPRINGBOOT
}
