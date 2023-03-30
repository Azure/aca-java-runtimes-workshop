// tag::adocHeader[]
package io.containerapps.javaruntime.workshop.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {
    "server.port=8803",
    "spring.datasource.url=jdbc:tc:postgresql:14-alpine://testcontainers/postgres",
    "spring.datasource.username=postgres",
    "spring.datasource.password=password"
})
class SpringbootResourceTest {

    private static String basePath = "http://localhost:8803/springboot";

    @Autowired
    private TestRestTemplate restTemplate;
// end::adocHeader[]

// tag::adocTestHello[]
    @Test
    public void testHelloEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody()).contains("Spring Boot: hello");
    }
// end::adocTestHello[]

    @Test
    public void testCpuEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/cpu?iterations=1", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: CPU consumption is done with")
            .endsWith("nano-seconds.");
    }

    @Test
    public void testCpuWithDBEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/cpu?iterations=1&db=true", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: CPU consumption is done with")
            .endsWith("The result is persisted in the database.");
    }

// tag::adocTestCPU[]
    @Test
    public void testCpuWithDBAndDescEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/cpu?iterations=1&db=true&dec=Java17", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: CPU consumption is done with")
            .doesNotContain("Java17")
            .endsWith("The result is persisted in the database.");
    }
// end::adocTestCPU[]

    @Test
    public void testMemoryEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/memory?bites=1", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: Memory consumption is done with")
            .endsWith("nano-seconds.");
    }

    @Test
    public void testMemoryWithDBEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/memory?bites=1&db=true", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: Memory consumption is done with")
            .endsWith("The result is persisted in the database.");
    }

// tag::adocTestMemory[]
    @Test
    public void testMemoryWithDBAndDescEndpoint() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/memory?bites=1&db=true&desc=Java17", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertThat(response.getBody())
            .startsWith("Spring Boot: Memory consumption is done with")
            .doesNotContain("Java17")
            .endsWith("The result is persisted in the database.");
    }
// end::adocTestMemory[]

// tag::adocTestStats[]
    @Test
    public void testStats() {
        ResponseEntity<String> response = this.restTemplate.
            getForEntity(basePath + "/stats", String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
// end::adocTestStats[]
}
