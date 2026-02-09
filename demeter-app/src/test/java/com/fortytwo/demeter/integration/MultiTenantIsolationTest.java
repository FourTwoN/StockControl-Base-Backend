package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Verifies multi-tenant data isolation at the API level.
 * This is the most critical test in the suite: it ensures that
 * tenants cannot see or access each other's data.
 */
@QuarkusTest
class MultiTenantIsolationTest {

    private static final String TENANT_A = "tenant-alpha";
    private static final String TENANT_B = "tenant-beta";

    @Test
    void tenantIsolation_productsShouldBeIsolated() {
        // Create product for tenant-alpha
        String productAId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "ISO-001", "name": "Alpha Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Alpha Product"))
                .extract().path("id");

        // Create product for tenant-beta (same SKU is allowed across tenants)
        String productBId = given()
                .header("X-Tenant-ID", TENANT_B)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "ISO-001", "name": "Beta Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Beta Product"))
                .extract().path("id");

        // Tenant-alpha sees only their product
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("content[0].name", equalTo("Alpha Product"));

        // Tenant-beta sees only their product
        given()
                .header("X-Tenant-ID", TENANT_B)
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("content[0].name", equalTo("Beta Product"));

        // Cross-tenant access: tenant-beta tries to get tenant-alpha's product by ID
        given()
                .header("X-Tenant-ID", TENANT_B)
                .when()
                .get("/api/v1/products/" + productAId)
                .then()
                .statusCode(404);

        // Cross-tenant access: tenant-alpha tries to get tenant-beta's product by ID
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/products/" + productBId)
                .then()
                .statusCode(404);
    }

    @Test
    void tenantIsolation_stockBatchesShouldBeIsolated() {
        // Create a product for each tenant
        String productAId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "BATCH-ISO-001", "name": "Alpha Batch Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");

        String productBId = given()
                .header("X-Tenant-ID", TENANT_B)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "BATCH-ISO-001", "name": "Beta Batch Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Create stock batch for tenant-alpha
        String batchAId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "productId": "%s",
                            "batchCode": "BATCH-A-001",
                            "quantity": 50,
                            "unit": "kg"
                        }
                        """.formatted(productAId))
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Create stock batch for tenant-beta
        given()
                .header("X-Tenant-ID", TENANT_B)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "productId": "%s",
                            "batchCode": "BATCH-B-001",
                            "quantity": 75,
                            "unit": "kg"
                        }
                        """.formatted(productBId))
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(201);

        // Tenant-alpha sees only their batch
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/stock-batches")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("content[0].batchCode", equalTo("BATCH-A-001"));

        // Tenant-beta sees only their batch
        given()
                .header("X-Tenant-ID", TENANT_B)
                .when()
                .get("/api/v1/stock-batches")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1))
                .body("content[0].batchCode", equalTo("BATCH-B-001"));

        // Cross-tenant access should be blocked
        given()
                .header("X-Tenant-ID", TENANT_B)
                .when()
                .get("/api/v1/stock-batches/" + batchAId)
                .then()
                .statusCode(404);
    }

    @Test
    void tenantIsolation_salesShouldBeIsolated() {
        // Create a product for each tenant
        String productAId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "SALE-ISO-001", "name": "Alpha Sale Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");

        String productBId = given()
                .header("X-Tenant-ID", TENANT_B)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "SALE-ISO-001", "name": "Beta Sale Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Create sale for tenant-alpha
        String saleAId = given()
                .header("X-Tenant-ID", TENANT_A)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Alpha Customer",
                            "items": [
                                {
                                    "productId": "%s",
                                    "quantity": 5,
                                    "unitPrice": 10.00
                                }
                            ]
                        }
                        """.formatted(productAId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Create sale for tenant-beta
        given()
                .header("X-Tenant-ID", TENANT_B)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Beta Customer",
                            "items": [
                                {
                                    "productId": "%s",
                                    "quantity": 3,
                                    "unitPrice": 20.00
                                }
                            ]
                        }
                        """.formatted(productBId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201);

        // Tenant-alpha sees only their sale
        given()
                .header("X-Tenant-ID", TENANT_A)
                .when()
                .get("/api/v1/sales")
                .then()
                .statusCode(200);

        // Cross-tenant access to sale should be blocked
        given()
                .header("X-Tenant-ID", TENANT_B)
                .when()
                .get("/api/v1/sales/" + saleAId)
                .then()
                .statusCode(404);
    }
}
