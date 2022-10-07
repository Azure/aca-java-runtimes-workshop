package io.containerapps.javaruntime.workshop.micronaut;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@MicronautTest
class MicronautApplicationTest {

    private PostgreSQLContainer postgreSQLContainer;

    @Inject
    EmbeddedApplication<?> application;

    @BeforeEach
    public void setUp() {
        postgreSQLContainer = new PostgreSQLContainer("postgres:14")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password");
    }

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }
}
