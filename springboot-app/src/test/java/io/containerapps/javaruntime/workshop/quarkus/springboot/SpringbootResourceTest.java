package io.containerapps.javaruntime.workshop.quarkus.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringbootResourceTest {

    @LocalServerPort
    private Integer port;

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("http://localhost:" + port + "/springboot")
          .then()
             .statusCode(200)
             .body(is("Hello from SpringBoot"));
    }

    @Test
    public void testCpuEndpoint() {
        given().param("iterations", 1)
          .when().get("/springboot/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("CPU consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testCpuWithDBEndpoint() {
        given().param("iterations", 1).param("db", true)
          .when().get("/springboot/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("CPU consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryEndpoint() {
        given().param("bites", 1)
          .when().get("/springboot/memory")
          .then()
             .statusCode(200)
             .body(startsWith("Memory consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testMemoryWithDBEndpoint() {
        given().param("bites", 1).param("db", true)
          .when().get("/springboot/memory")
          .then()
             .statusCode(200)
             .body(startsWith("Memory consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }
}
