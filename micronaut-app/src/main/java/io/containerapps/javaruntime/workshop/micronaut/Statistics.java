package io.containerapps.javaruntime.workshop.micronaut;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Duration;
import java.time.Instant;

@Entity
public class Statistics{

    @GeneratedValue
    @Id
    private Long id;
    public Instant doneAt = Instant.now();
    public Framework framework = Framework.MICRONAUT;
    public Type type;
    public String parameter;
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
