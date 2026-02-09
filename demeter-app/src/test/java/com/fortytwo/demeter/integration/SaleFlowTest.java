package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * End-to-end sale flow test.
 * Exercises the full lifecycle: product creation, stock batch,
 * sale creation, completion, and cancellation rules.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SaleFlowTest {

    private static final String TENANT = "tenant-sale-flow";

    private static String productId;
    private static String batchId;
    private static String saleId;
    private static String cancelTestSaleId;

    @Test
    @Order(1)
    void setup_createProduct() {
        productId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {"sku": "SALE-PROD-001", "name": "Sale Flow Product"}
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    @Order(2)
    void setup_createStockBatch() {
        batchId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "productId": "%s",
                            "batchCode": "SALE-BATCH-001",
                            "quantity": 100,
                            "unit": "units"
                        }
                        """.formatted(productId))
                .when()
                .post("/api/v1/stock-batches")
                .then()
                .statusCode(201)
                .body("quantity", equalTo(100))
                .extract().path("id");
    }

    @Test
    @Order(3)
    void createSale_shouldReturn201() {
        saleId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "John Doe",
                            "customerEmail": "john@example.com",
                            "notes": "Integration test sale",
                            "items": [
                                {
                                    "productId": "%s",
                                    "batchId": "%s",
                                    "quantity": 10,
                                    "unitPrice": 25.50
                                }
                            ]
                        }
                        """.formatted(productId, batchId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("saleNumber", notNullValue())
                .body("status", equalTo("PENDING"))
                .body("customerName", equalTo("John Doe"))
                .body("customerEmail", equalTo("john@example.com"))
                .body("totalAmount", equalTo(255.00f))
                .body("items.size()", equalTo(1))
                .body("items[0].quantity", equalTo(10.0f))
                .body("items[0].unitPrice", equalTo(25.50f))
                .extract().path("id");
    }

    @Test
    @Order(4)
    void getSale_shouldReturnCreated() {
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/sales/" + saleId)
                .then()
                .statusCode(200)
                .body("id", equalTo(saleId))
                .body("status", equalTo("PENDING"))
                .body("items.size()", equalTo(1));
    }

    @Test
    @Order(5)
    void updateSale_shouldModifyCustomerInfo() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Jane Doe",
                            "notes": "Updated sale notes"
                        }
                        """)
                .when()
                .put("/api/v1/sales/" + saleId)
                .then()
                .statusCode(200)
                .body("customerName", equalTo("Jane Doe"))
                .body("notes", equalTo("Updated sale notes"));
    }

    @Test
    @Order(6)
    void completeSale_shouldChangeStatusToCompleted() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/sales/" + saleId + "/complete")
                .then()
                .statusCode(200)
                .body("status", equalTo("COMPLETED"));
    }

    @Test
    @Order(7)
    void cancelCompletedSale_shouldFail() {
        // Attempting to cancel a COMPLETED sale should fail
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/sales/" + saleId + "/cancel")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(8)
    void completeAlreadyCompletedSale_shouldFail() {
        // Attempting to complete an already COMPLETED sale should fail
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/sales/" + saleId + "/complete")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(9)
    void cancelPendingSale_shouldSucceed() {
        // Create a new sale that we will cancel
        cancelTestSaleId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Cancel Customer",
                            "items": [
                                {
                                    "productId": "%s",
                                    "batchId": "%s",
                                    "quantity": 5,
                                    "unitPrice": 15.00
                                }
                            ]
                        }
                        """.formatted(productId, batchId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("status", equalTo("PENDING"))
                .extract().path("id");

        // Cancel the pending sale
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/sales/" + cancelTestSaleId + "/cancel")
                .then()
                .statusCode(200)
                .body("status", equalTo("CANCELLED"));
    }

    @Test
    @Order(10)
    void completeCancelledSale_shouldFail() {
        // Attempting to complete a CANCELLED sale should fail
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/sales/" + cancelTestSaleId + "/complete")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(11)
    void listSales_shouldReturnPaged() {
        given()
                .header("X-Tenant-ID", TENANT)
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/api/v1/sales")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(12)
    void createSale_emptyItems_shouldReturn400() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Bad Sale",
                            "items": []
                        }
                        """)
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(13)
    void createSaleWithMultipleItems_shouldCalculateTotal() {
        given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Multi Item Customer",
                            "items": [
                                {
                                    "productId": "%s",
                                    "batchId": "%s",
                                    "quantity": 2,
                                    "unitPrice": 10.00
                                },
                                {
                                    "productId": "%s",
                                    "batchId": "%s",
                                    "quantity": 3,
                                    "unitPrice": 20.00
                                }
                            ]
                        }
                        """.formatted(productId, batchId, productId, batchId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .body("items.size()", equalTo(2))
                .body("totalAmount", equalTo(80.00f));
    }

    @Test
    @Order(14)
    void deleteSale_shouldReturn204() {
        // Create a sale to delete
        String deleteSaleId = given()
                .header("X-Tenant-ID", TENANT)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "customerName": "Delete Customer",
                            "items": [
                                {
                                    "productId": "%s",
                                    "quantity": 1,
                                    "unitPrice": 5.00
                                }
                            ]
                        }
                        """.formatted(productId))
                .when()
                .post("/api/v1/sales")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .delete("/api/v1/sales/" + deleteSaleId)
                .then()
                .statusCode(204);

        // Verify it is gone
        given()
                .header("X-Tenant-ID", TENANT)
                .when()
                .get("/api/v1/sales/" + deleteSaleId)
                .then()
                .statusCode(404);
    }
}
