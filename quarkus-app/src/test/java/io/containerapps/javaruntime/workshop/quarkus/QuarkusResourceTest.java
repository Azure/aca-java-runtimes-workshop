// tag::adocHeader[]
package io.containerapps.javaruntime.workshop.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class QuarkusResourceTest {
// end::adocHeader[]

// tag::adocTestHello[]
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/quarkus")
          .then()
            .statusCode(200)
            .body(is("Quarkus: hello"));
    }
// end::adocTestHello[]

    @Test
    void testCpuEndpoint() {
        given().param("iterations", 1)
          .when().get("/quarkus/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: CPU consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    void testCpuWithDBEndpoint() {
        given().param("iterations", 1).param("db", true)
          .when().get("/quarkus/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: CPU consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }

// tag::adocTestCPU[]
    @Test
    void testCpuWithDBAndDescEndpoint() {
        given().param("iterations", 1).param("db", true).param("desc", "Java17")
          .when().get("/quarkus/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: CPU consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }
// end::adocTestCPU[]

    @Test
    void testMemoryEndpoint() {
        given().param("bites", 1)
          .when().get("/quarkus/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: Memory consumption is done with"))
            .body(endsWith("nano-seconds."));
    }

    @Test
    void testMemoryWithDBEndpoint() {
        given().param("bites", 1).param("db", true)
          .when().get("/quarkus/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: Memory consumption is done with"))
            .body(endsWith("The result is persisted in the database."));
    }

// tag::adocTestMemory[]
    @Test
    void testMemoryWithDBAndDescEndpoint() {
        given().param("bites", 1).param("db", true).param("desc", "Java17")
          .when().get("/quarkus/memory")
          .then()
            .statusCode(200)
            .body(startsWith("Quarkus: Memory consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }
// end::adocTestMemory[]

// tag::adocTestStats[]
    @Test
    void testStats() {
        given()
          .when().get("/quarkus/stats")
          .then()
            .statusCode(200);
    }
// end::adocTestStats[]
}
