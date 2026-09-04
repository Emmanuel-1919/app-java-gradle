package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "APP_ENV=qa",
        "GIT_BRANCH=qa",
        "GIT_COMMIT=abcdef1234",
        "APP_VERSION=1.0.0"
})
class InfoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthReturnsOk() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("http://localhost:" + port + "/health", String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("ok");
    }

    @Test
    void indexShowsEnvironmentAndCommit() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("http://localhost:" + port + "/", String.class);
        assertThat(response.getBody()).contains("QA");
        assertThat(response.getBody()).contains("abcdef1");
    }
}