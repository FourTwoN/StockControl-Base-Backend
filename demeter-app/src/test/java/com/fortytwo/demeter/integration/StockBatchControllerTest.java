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
class StockBatchControllerTest {

    private static final String TENANT = "tenant-stock-batch";

    private static String productId;
    private static String batchId;

    @Test
    @Order(1)
    void setup_createProduct() {
        productId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "SB-PROD-001", "name": "Stock Batch Test Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    @Order(2)
    void createStockBatch_shouldReturn201() {
        batchId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "productId": "%s",
                            "batchCode": "BATCH-001",
                            "quantity": 100,
                            "unit": "kg"
                        }
                        """.formatted(productId))
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("batchCode", equalTo("BATCH-001"))
                .body("quantity", equalTo(100))
                .body("unit", equalTo("kg"))
                .body("status", equalTo("ACTIVE"))
                .body("productId", equalTo(productId))
                .extract().path("id");
    }

    @Test
    @Order(3)
    void getStockBatch_shouldReturnCreated() {
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/stock-batches/" + batchId)
                .then()
                .statusCode(200)
                .body("id", equalTo(batchId))
                .body("batchCode", equalTo("BATCH-001"))
                .body("productName", equalTo("Stock Batch Test Product"))
                .body("createdAt", notNullValue());
    }

    @Test
    @Order(4)
    void listStockBatches_shouldReturnPaged() {
        given()
                .header("X-Tenant-ID", TENANT)
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/api/v1/stock-batches")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(1))
                .body("page", equalTo(0))
                .body("size", equalTo(20));
    }

    @Test
    @Order(5)
    void getByProduct_shouldReturnBatches() {
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/stock-batches/by-product/" + productId)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].batchCode", equalTo("BATCH-001"));
    }

    @Test
    @Order(6)
    void getByStatus_shouldReturnActiveBatches() {
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/stock-batches/by-status/ACTIVE")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(7)
    void updateStockBatch_shouldModify() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "unit": "lbs",
                            "status": "QUARANTINED"
                        }
                        """)
                .when()
                .put("/api/v1/stock-batches/" + batchId)
                .then()
                .statusCode(200)
                .body("unit", equalTo("lbs"))
                .body("status", equalTo("QUARANTINED"));
    }

    @Test
    @Order(8)
    void deleteStockBatch_shouldReturn204() {
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .delete("/api/v1/stock-batches/" + batchId)
                .then()
                .statusCode(204);

        // Verify it is gone
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/stock-batches/" + batchId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    void createStockBatch_invalidProduct_shouldReturn404() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "productId": "00000000-0000-0000-0000-000000000000",
                            "batchCode": "BATCH-INVALID",
                            "quantity": 10,
                            "unit": "kg"
                        }
                        """)
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    void createStockBatch_missingRequired_shouldReturn400() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "unit": "kg"
                        }
                        """)
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(400);
    }
}
