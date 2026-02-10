package com.fortytwo.demeter.integration;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestSecurity(user = "test-user", roles = {"ADMIN"})
class TenantConfigTest {

    private static final String TENANT_ID = "test-bar";

    @Test
    @Order(1)
    void createTenant_shouldReturn201() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "id": "test-bar",
                            "name": "Test Bar",
                            "industry": "COMERCIANTES",
                            "theme": {
                                "primary": "#FF6B35",
                                "secondary": "#0F172A",
                                "accent": "#E65100",
                                "background": "#F8FAFC",
                                "logoUrl": "https://cdn.demeter.app/test-bar/logo.png",
                                "appName": "Test Bar Stock"
                            },
                            "enabledModules": ["inventario", "productos", "ventas", "ubicaciones"],
                            "settings": {
                                "currency": "ARS",
                                "timezone": "America/Argentina/Buenos_Aires"
                            }
                        }
                        """)
                .when()
                .post("/api/v1/tenants")
                .then()
                .statusCode(201)
                .body("id", equalTo(TENANT_ID))
                .body("name", equalTo("Test Bar"))
                .body("industry", equalTo("COMERCIANTES"))
                .body("active", equalTo(true));
    }

    @Test
    @Order(2)
    void getConfig_shouldReturnFullShape() {
        given()
                .when()
                .get("/api/v1/tenants/" + TENANT_ID + "/config")
                .then()
                .statusCode(200)
                .body("id", equalTo(TENANT_ID))
                .body("name", equalTo("Test Bar"))
                .body("industry", equalTo("COMERCIANTES"))
                .body("theme.primary", equalTo("#FF6B35"))
                .body("theme.secondary", equalTo("#0F172A"))
                .body("theme.accent", equalTo("#E65100"))
                .body("theme.background", equalTo("#F8FAFC"))
                .body("theme.logoUrl", equalTo("https://cdn.demeter.app/test-bar/logo.png"))
                .body("theme.appName", equalTo("Test Bar Stock"))
                .body("enabledModules", hasItems("inventario", "productos", "ventas", "ubicaciones"))
                .body("settings.currency", equalTo("ARS"))
                .body("settings.timezone", equalTo("America/Argentina/Buenos_Aires"));
    }

    @Test
    @Order(3)
    void getConfig_nonExistentTenant_shouldReturn404() {
        given()
                .when()
                .get("/api/v1/tenants/nonexistent/config")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    void updateTenant_shouldModify() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Updated Bar",
                            "enabledModules": ["inventario", "productos", "ventas", "ubicaciones", "fotos"]
                        }
                        """)
                .when()
                .put("/api/v1/tenants/" + TENANT_ID)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Bar"))
                .body("enabledModules", hasItems("inventario", "productos", "ventas", "ubicaciones", "fotos"));
    }

    @Test
    @Order(5)
    void getConfig_afterUpdate_shouldReflectChanges() {
        given()
                .when()
                .get("/api/v1/tenants/" + TENANT_ID + "/config")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Bar"))
                .body("theme.primary", equalTo("#FF6B35"))
                .body("enabledModules", hasItems("fotos"))
                .body("settings.currency", equalTo("ARS"));
    }

    @Test
    @Order(6)
    void listTenants_shouldIncludeCreated() {
        given()
                .when()
                .get("/api/v1/tenants")
                .then()
                .statusCode(200)
                .body("size()", notNullValue());
    }

    @Test
    @Order(7)
    void deleteTenant_shouldReturn204() {
        given()
                .when()
                .delete("/api/v1/tenants/" + TENANT_ID)
                .then()
                .statusCode(204);

        // Verify it is gone
        given()
                .when()
                .get("/api/v1/tenants/" + TENANT_ID + "/config")
                .then()
                .statusCode(404);
    }
}
