package io.containerapps.javaruntime.workshop.springboot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class SpringbootResourceTest {
    @LocalServerPort
    private int port;
    private String basePath;

    @BeforeAll
    public void setup() {
        basePath = "http://localhost:" + port + "/springboot";
    }

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
