package io.containerapps.javaruntime.workshop.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.time.Duration;
import java.time.Instant;

@Entity
public class Statistics extends PanacheEntity {

    public Instant doneAt = Instant.now();
    public Framework framework = Framework.QUARKUS;
    public Type type;
    public String parameter;
    public Duration duration;
}

enum Type {
    CPU, MEMORY
}

enum Framework {
    QUARKUS, MICRONAUT, SPRINGBOOT
}
