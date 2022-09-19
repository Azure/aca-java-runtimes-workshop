package io.containerapps.javaruntime.workshop.quarkus.springboot;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Duration;
import java.time.Instant;

@Entity
public class Statistics{

    @GeneratedValue
    @Id
    private Long id;
    public Instant doneAt = Instant.now();
    public Framework framework = Framework.QUARKUS;
    public Type type;
    public String parameter;
    public Duration duration;

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
