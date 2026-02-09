package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

/**
 * Tests for OpenAPI and Swagger UI availability.
 * Ensures the API documentation is generated and accessible.
 */
@QuarkusTest
class OpenApiTest {

    @Test
    void openApi_shouldReturn200() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("Demeter AI 2.0 API"));
    }

    @Test
    void openApi_shouldContainProductsPath() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("/api/v1/products"));
    }

    @Test
    void openApi_shouldContainSalesPath() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("/api/v1/sales"));
    }

    @Test
    void openApi_shouldContainStockBatchesPath() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/q/openapi")
                .then()
                .statusCode(200)
                .body(containsString("/api/v1/stock-batches"));
    }

    @Test
    void swaggerUi_shouldBeAccessible() {
        given()
                .when()
                .get("/q/swagger-ui/")
                .then()
                .statusCode(200)
                .body(containsString("swagger-ui"));
    }
}
