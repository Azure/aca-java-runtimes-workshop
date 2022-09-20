package io.containerapps.javaruntime.workshop.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class QuarkusResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/quarkus")
          .then()
             .statusCode(200)
             .body(is("Quarkus: hello"));
    }

    @Test
    public void testCpuEndpoint() {
        given().param("iterations", 1)
          .when().get("/quarkus/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("Quarkus: CPU consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testCpuWithDBEndpoint() {
        given().param("iterations", 1).param("db", true)
          .when().get("/quarkus/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("Quarkus: CPU consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryEndpoint() {
        given().param("bites", 1)
          .when().get("/quarkus/memory")
          .then()
             .statusCode(200)
             .body(startsWith("Quarkus: Memory consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testMemoryWithDBEndpoint() {
        given().param("bites", 1).param("db", true)
          .when().get("/quarkus/memory")
          .then()
             .statusCode(200)
             .body(startsWith("Quarkus: Memory consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }
}
