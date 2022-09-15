package io.containerapps.javaruntime.workshop.micronaut;

import java.time.Duration;
import java.time.Instant;

public class Statistics{

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
