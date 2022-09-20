package io.containerapps.javaruntime.workshop.quarkus.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SpringbootResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("http://localhost:8080/springboot")
          .then()
             .statusCode(200)
             .body(is("SpringBoot: hello"));
    }

    @Test
    public void testCpuEndpoint() {
        given().param("iterations", 1)
          .when().get("/springboot/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: CPU consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testCpuWithDBEndpoint() {
        given().param("iterations", 1).param("db", true)
          .when().get("/springboot/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: CPU consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryEndpoint() {
        given().param("bites", 1)
          .when().get("/springboot/memory")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: Memory consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testMemoryWithDBEndpoint() {
        given().param("bites", 1).param("db", true)
          .when().get("/springboot/memory")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: Memory consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }
}
