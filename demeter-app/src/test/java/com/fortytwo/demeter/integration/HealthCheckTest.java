package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for health check endpoints.
 * Verifies that liveness and readiness probes respond correctly.
 */
@QuarkusTest
class HealthCheckTest {

    @Test
    void liveness_shouldReturn200() {
        given()
                .when()
                .get("/q/health/live")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks", notNullValue());
    }

    @Test
    void readiness_shouldReturn200() {
        given()
                .when()
                .get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks", notNullValue());
    }

    @Test
    void healthGroup_shouldReturn200() {
        given()
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }
}
