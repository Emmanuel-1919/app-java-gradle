package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoController {

    @Value("${APP_ENV:dev}")
    private String ambiente;

    @Value("${GIT_BRANCH:unknown}")
    private String rama;

    @Value("${GIT_COMMIT:unknown}")
    private String commit;

    @Value("${APP_VERSION:0.0.1}")
    private String version;

    private String colorFor(String ambiente) {
        return switch (ambiente) {
            case "dev" -> "#2563eb";
            case "qa" -> "#f97316";
            case "prod" -> "#16a34a";
            default -> "#6b7280";
        };
    }

    private String shortCommit() {
        return commit.length() > 7 ? commit.substring(0, 7) : commit;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return """
            <html>
            <head><title>App Java Gradle - %s</title></head>
            <body style="background-color:%s; font-family: sans-serif; color:white; text-align:center; padding-top:50px;">
                <h1 style="font-size:64px;">%s</h1>
                <p>Rama: %s</p>
                <p>Commit: %s</p>
                <p>Versión: %s</p>
                <p>Stack: Java + Spring Boot + Gradle</p>
            </body>
            </html>
            """.formatted(ambiente, colorFor(ambiente), ambiente.toUpperCase(), rama, shortCommit(), version);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}