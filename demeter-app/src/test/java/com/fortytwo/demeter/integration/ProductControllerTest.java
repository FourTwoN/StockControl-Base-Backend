package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

    private static final String TENANT_A = "tenant-a";

    private static String createdProductId;

    @Test
    @Order(1)
    void createProduct_shouldReturn201() {
        createdProductId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "sku": "PROD-001",
                            "name": "Test Product Alpha",
                            "description": "A product created by integration test"
                        }
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("sku", equalTo("PROD-001"))
                .body("name", equalTo("Test Product Alpha"))
                .body("description", equalTo("A product created by integration test"))
                .body("state", equalTo("ACTIVE"))
                .extract().path("id");
    }

    @Test
    @Order(2)
    void getProduct_shouldReturnCreated() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/products/" + createdProductId)
                .then()
                .statusCode(200)
                .body("id", equalTo(createdProductId))
                .body("sku", equalTo("PROD-001"))
                .body("name", equalTo("Test Product Alpha"))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue());
    }

    @Test
    @Order(3)
    void listProducts_shouldReturnPaged() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(1))
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("totalElements", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(4)
    void updateProduct_shouldModify() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Updated Product Alpha",
                            "description": "Updated description"
                        }
                        """)
                .when()
                .put("/api/v1/products/" + createdProductId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Product Alpha"))
                .body("description", equalTo("Updated description"))
                .body("sku", equalTo("PROD-001"));
    }

    @Test
    @Order(5)
    void deleteProduct_shouldReturn204() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .delete("/api/v1/products/" + createdProductId)
                .then()
                .statusCode(204);

        // Verify it is gone
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/products/" + createdProductId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(6)
    void createProduct_missingRequiredFields_shouldReturn400() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "description": "Missing sku and name"
                        }
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void getProduct_nonExistentId_shouldReturn404() {
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/products/00000000-0000-0000-0000-000000000000")
                .then()
                .statusCode(404);
    }
}
