package io.containerapps.javaruntime.workshop.quarkus.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {
    "server.port=8803",
    "spring.datasource.url=jdbc:tc:postgresql:14-alpine://testcontainers/postgres",
    "spring.datasource.username=postgres",
    "spring.datasource.password=password"
})
public class SpringbootResourceTest {

    private static String basePath = "http://localhost:8803/springboot";

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get(basePath)
          .then()
             .statusCode(200)
             .body(is("SpringBoot: hello"));
    }

    @Test
    public void testCpuEndpoint() {
        given().param("iterations", 1)
          .when().get(basePath + "/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: CPU consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testCpuWithDBEndpoint() {
        given().param("iterations", 1).param("db", true)
          .when().get(basePath + "/cpu")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: CPU consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testCpuWithDBAndDescEndpoint() {
        given().param("iterations", 1).param("db", true).param("desc", "Java17")
          .when().get(basePath + "/cpu")
          .then()
            .statusCode(200)
            .body(startsWith("SpringBoot: CPU consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryEndpoint() {
        given().param("bites", 1)
          .when().get(basePath + "/memory")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: Memory consumption is done with"))
             .body(endsWith("nano-seconds."));
    }

    @Test
    public void testMemoryWithDBEndpoint() {
        given().param("bites", 1).param("db", true)
          .when().get(basePath + "/memory")
          .then()
             .statusCode(200)
             .body(startsWith("SpringBoot: Memory consumption is done with"))
             .body(endsWith("The result is persisted in the database."));
    }

    @Test
    public void testMemoryWithDBAndDescEndpoint() {
        given().param("bites", 1).param("db", true).param("desc", "Java17")
          .when().get(basePath + "/memory")
          .then()
            .statusCode(200)
            .body(startsWith("SpringBoot: Memory consumption is done with"))
            .body(not(containsString("Java17")))
            .body(endsWith("The result is persisted in the database."));
    }
}
