# Demeter Backend — Development Guidelines

## Overview

This is the Demeter AI 2.0 Backend — a multi-tenant stock control platform built with:
- **Java 25** + **Quarkus 3.31.2**
- **PostgreSQL 17** with Row-Level Security
- **Virtual Threads** (Project Loom) enabled globally
- **Hibernate Panache** for data access

---

## Critical Conventions

### 1. Virtual Threads (ALWAYS USE)

Virtual Threads are **enabled globally**. Write simple blocking code — the JVM handles concurrency automatically.

```properties
# application.properties
quarkus.virtual-threads.enabled=true
```

**DO:**
```java
@GET
public List<Product> list() {
    return productRepository.listAll();  // Blocking call on Virtual Thread
}
```

**DON'T:**
```java
// NO Reactive/Mutiny — we don't need it with Virtual Threads
@GET
public Uni<List<Product>> list() {
    return productRepository.listAll();  // Unnecessary complexity
}
```

**Why Virtual Threads?**
- Simple blocking code style
- Millions of concurrent requests (~1KB per thread vs ~1MB traditional)
- Clear stack traces for debugging
- No callback hell or Reactive complexity

### 2. Panache Queries (Choose the Right Tool)

| Complexity | Use | Example |
|------------|-----|---------|
| CRUD basico | Panache built-in | `findById()`, `listAll()`, `persist()` |
| Filtros simples | PQL | `list("price > ?1", 100)` |
| Joins simples | JPQL en `find()` | `find("SELECT p FROM Product p JOIN FETCH p.category")` |
| Joins complejos | JPQL con `createQuery()` | Agregaciones, subqueries, projection DTOs |
| PostgreSQL features | SQL Nativo | CTEs, window functions, JSONB, `current_setting()` |

**Simple queries:**
```java
// PQL - Panache Query Language
List<Product> active = productRepository.list("active = ?1", true);

// Named parameters
List<Product> filtered = productRepository.list(
    "category.name = :cat and price > :minPrice",
    Parameters.with("cat", "Bebidas").and("minPrice", 100)
);
```

**Complex queries with joins:**
```java
public List<ProductReport> getReport() {
    return getEntityManager().createQuery("""
        SELECT new com.fortytwo.demeter.dto.ProductReport(
            p.id, p.name, SUM(sb.quantity)
        )
        FROM Product p
        LEFT JOIN StockBatch sb ON sb.product = p
        GROUP BY p.id, p.name
        ORDER BY SUM(sb.quantity) DESC
        """, ProductReport.class)
        .getResultList();
}
```

**Native SQL for PostgreSQL-specific features:**
```java
// JSONB, CTEs, window functions, RLS functions
public List<Object[]> getWithJsonb() {
    return getEntityManager().createNativeQuery("""
        SELECT name, custom_attributes->>'origin'
        FROM stock_batches
        WHERE tenant_id = current_setting('app.current_tenant')
        """)
        .getResultList();
}
```

### 3. Multi-Tenancy (3-Layer Isolation)

Every entity extends `BaseEntity` which includes `tenant_id`. Isolation is enforced at 3 levels:

| Layer | Mechanism |
|-------|-----------|
| Application | `DemeterTenantResolver` extracts tenant from JWT/header |
| ORM | Hibernate `@TenantId` adds WHERE clause automatically |
| Database | PostgreSQL RLS policies enforce at DB level |

**Never hardcode tenant filtering** — Hibernate does it automatically:
```java
// CORRECT - Hibernate adds WHERE tenant_id = ? automatically
productRepository.listAll();

// WRONG - Don't filter manually
productRepository.list("tenantId = ?1", tenantId);
```

### 4. Immutability (CRITICAL)

**ALWAYS create new objects, NEVER mutate:**

```java
// WRONG - Mutation
public Product updatePrice(Product product, BigDecimal newPrice) {
    product.price = newPrice;  // MUTATION!
    return product;
}

// CORRECT - Create new instance
public Product updatePrice(Product product, BigDecimal newPrice) {
    return Product.builder()
        .id(product.id)
        .name(product.name)
        .price(newPrice)  // Only this changes
        .build();
}
```

For entities, use `@PreUpdate` for audit fields, not manual mutation.

### 5. Error Handling

Use domain exceptions, not generic ones:

```java
// Domain exceptions in demeter-common
throw new EntityNotFoundException("Product", productId);
throw new BusinessRuleException("Cannot complete sale with zero items");
throw new TenantAccessDeniedException(tenantId);
```

### 6. DTOs and Records

Use Java Records for DTOs:

```java
public record CreateProductRequest(
    @NotBlank String sku,
    @NotBlank String name,
    @Positive BigDecimal price
) {}

public record ProductResponse(
    UUID id,
    String sku,
    String name,
    BigDecimal price,
    Instant createdAt
) {
    public static ProductResponse from(Product entity) {
        return new ProductResponse(
            entity.id,
            entity.sku,
            entity.name,
            entity.price,
            entity.createdAt
        );
    }
}
```

---

## Project Structure

```
demeter-app (Quarkus main)
├── demeter-common         # BaseEntity, tenant, auth, exceptions, DTOs
├── demeter-productos      # Products, categories, families
├── demeter-inventario     # Stock batches, movements
├── demeter-ventas         # Sales lifecycle
├── demeter-costos         # Cost tracking
├── demeter-precios        # Price lists
├── demeter-ubicaciones    # Warehouses, bins
├── demeter-empaquetado    # Packaging catalogs
├── demeter-usuarios       # Users, roles
├── demeter-analytics      # Read-only analytics
├── demeter-fotos          # Photo processing (DLC)
└── demeter-chatbot        # AI chat (DLC)
```

---

## Testing

Run tests with:
```bash
./gradlew :demeter-app:test
```

Tests use:
- **JUnit 5** for test framework
- **REST Assured** for API testing
- **Testcontainers** for PostgreSQL
- **@TestSecurity** for mocking authentication

Example:
```java
@QuarkusTest
@TestSecurity(user = "test-user", roles = "ADMIN")
class ProductControllerTest {

    @Test
    void shouldCreateProduct() {
        given()
            .header("X-Tenant-ID", "test-tenant")
            .contentType(ContentType.JSON)
            .body(new CreateProductRequest("SKU-001", "Test", BigDecimal.TEN))
        .when()
            .post("/api/v1/products")
        .then()
            .statusCode(201)
            .body("sku", equalTo("SKU-001"));
    }
}
```

---

## Cloud Tasks Integration

For async ML processing, use Cloud Tasks:

```java
@Inject
CloudTasksService cloudTasksService;

public void processPhoto(UUID sessionId, String imageUrl) {
    cloudTasksService.enqueue(
        new ProcessingTaskRequest(tenantId, sessionId, imageUrl, "FULL_PIPELINE")
    );
}
```

In dev mode (`%dev`), Cloud Tasks is disabled and logs tasks without creating them.

---

## Common Commands

```bash
# Run in dev mode
./gradlew :demeter-app:quarkusDev

# Run tests
./gradlew :demeter-app:test

# Build for production
./gradlew :demeter-app:quarkusBuild

# Check active modules
./gradlew :demeter-app:printDemeterModules
```

---

## Key Files

| File | Purpose |
|------|---------|
| `application.properties` | All configuration (profiles: dev, staging, prod) |
| `db/migration/V*.sql` | Flyway migrations |
| `BaseEntity.java` | Base class with id, tenantId, timestamps |
| `DemeterTenantResolver.java` | Extracts tenant from JWT/header |
| `RlsConnectionCustomizer.java` | Sets RLS context on each connection |
