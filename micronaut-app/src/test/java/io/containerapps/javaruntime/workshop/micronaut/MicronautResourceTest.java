package io.containerapps.javaruntime.workshop.micronaut;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;

@MicronautTest
class MicronautResourceTest {

    @Test
    public void testHelloEndpoint(RequestSpecification spec) {
        spec
            .when().get("/micronaut")
            .then()
            .statusCode(200)
            .body(is("Hello from Micronaut"));
    }

    @Test
    public void testCpuEndpoint(RequestSpecification spec) {
        spec.param("iterations", 1)
            .when().get("/micronaut/cpu")
            .then()
            .statusCode(200)
            .body(startsWith("CPU consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    @Disabled
    public void testCpuWithDBEndpoint(RequestSpecification spec) {
        spec.param("iterations", 1).param("db", true)
            .when().get("/micronaut/cpu")
            .then()
            .statusCode(200)
            .body(startsWith("CPU consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryEndpoint(RequestSpecification spec) {
        spec.param("bites", 1)
            .when().get("/micronaut/memory")
            .then()
            .statusCode(200)
            .body(startsWith("Memory consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    @Disabled
    public void testMemoryWithDBEndpoint(RequestSpecification spec) {
        spec.param("bites", 1).param("db", true)
            .when().get("/micronaut/memory")
            .then()
            .statusCode(200)
            .body(startsWith("Memory consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }
}
