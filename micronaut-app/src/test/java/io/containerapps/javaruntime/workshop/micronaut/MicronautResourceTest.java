// tag::adocHeader[]
package io.containerapps.javaruntime.workshop.micronaut;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@Testcontainers
@MicronautTest
class MicronautResourceTest {
// end::adocHeader[]

    private PostgreSQLContainer postgreSQLContainer;

    @BeforeEach
    public void setUp() {
        postgreSQLContainer = new PostgreSQLContainer("postgres:14")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password");
    }

// tag::adocTestHello[]
    @Test
    public void testHelloEndpoint(RequestSpecification spec) {
        spec
          .when().get("/micronaut")
          .then()
            .statusCode(200)
            .body(is("Micronaut: hello"));
    }
// end::adocTestHello[]

    @Test
    public void testCpuEndpoint(RequestSpecification spec) {
        spec.param("iterations", 1)
          .when().get("/micronaut/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: CPU consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    public void testCpuWithDBEndpoint(RequestSpecification spec) {
        spec.param("iterations", 1).param("db", true)
          .when().get("/micronaut/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: CPU consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }

// tag::adocTestCPU[]
    @Test
    public void testCpuWithDBAndDescEndpoint() {
        given().param("iterations", 1).param("db", true).param("desc", "Java17")
          .when().get("/micronaut/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: CPU consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }
// end::adocTestCPU[]

    @Test
    public void testMemoryEndpoint(RequestSpecification spec) {
        spec.param("bites", 1)
          .when().get("/micronaut/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: Memory consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    public void testMemoryWithDBEndpoint(RequestSpecification spec) {
        spec.param("bites", 1).param("db", true)
          .when().get("/micronaut/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: Memory consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }

// tag::adocTestMemory[]
    @Test
    public void testMemoryWithDBAndDescEndpoint() {
        given().param("bites", 1).param("db", true).param("desc", "Java17")
          .when().get("/micronaut/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Micronaut: Memory consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }
// end::adocTestMemory[]
}
