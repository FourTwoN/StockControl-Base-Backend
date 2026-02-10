# Demeter AI 2.0 — Backend

**Multi-tenant stock control and analytics platform for same-industry SaaS.**

Java 25 &middot; Quarkus 3.31.2 &middot; PostgreSQL 17 &middot; Gradle 9.3.1

---

## What is Demeter?

Demeter is a backend platform designed so that **multiple businesses within the same industry** share a single codebase and database while keeping their data, catalogs, workflows, and configurations completely isolated. Each tenant (business) gets its own universe of products, warehouses, pricing, and analytics — without needing a separate deployment.

### Who is it for?

Any industry where multiple companies manage physical stock: agriculture, food production, chemicals, retail, manufacturing, logistics, pharmaceuticals. The platform adapts to each tenant's specific vocabulary, processes, and data requirements.

---

## Architecture

### 13 Modules

```
demeter-app (Quarkus main)
├── demeter-productos      Products, categories, families, density parameters
├── demeter-inventario     Stock batches, movements, custom attributes
├── demeter-ventas         Sales lifecycle (create → complete/cancel)
├── demeter-costos         Cost tracking (product/batch level)
├── demeter-precios        Price lists with versioning and tiered pricing
├── demeter-ubicaciones    Warehouses, areas, locations, bins (custom hierarchy)
├── demeter-empaquetado    Packaging types, materials, colors, catalogs
├── demeter-usuarios       Users, roles, OIDC mapping
├── demeter-analytics      7 read-only dashboard endpoints
├── demeter-fotos          Photo processing sessions, AI detections (DLC)
├── demeter-chatbot        Conversational AI with tool execution (DLC)
└── demeter-common         BaseEntity, tenant resolver, RLS, auth, exceptions
```

### 3-Layer Tenant Isolation

Every row in the database belongs to exactly one tenant. Isolation is enforced at three independent levels — all three must fail simultaneously for data to leak:

| Layer | Mechanism | What it does |
|-------|-----------|-------------|
| **Application** | `DemeterTenantResolver` | Extracts `tenant_id` from JWT claim or `X-Tenant-ID` header |
| **ORM** | Hibernate `@TenantId` discriminator | Appends `WHERE tenant_id = ?` to every query automatically |
| **Database** | PostgreSQL Row-Level Security | RLS policies on all 40+ tables enforce `tenant_id = current_setting('app.current_tenant')` |

```
Request → JWT/Header → TenantResolver → TenantContext (request-scoped)
                                              │
                        ┌─────────────────────┼─────────────────────┐
                        ▼                     ▼                     ▼
                   Hibernate              set_config()          RLS Policy
                   @TenantId           app.current_tenant     USING(tenant_id)
                   (ORM filter)        (connection var)       (DB enforcement)
```

---

## How Customizable Is It Per Tenant?

This is the core design question. Demeter is built so that **two competing businesses in the same industry** can use the same deployment with completely different configurations. Here's exactly what each tenant controls independently:

### 1. Catalogs and Master Data

Every catalog entity is tenant-scoped. Tenant A's product categories are invisible to Tenant B.

| Catalog | What it means |
|---------|--------------|
| **Product categories & families** | Tenant A: "Solvents", "Acids", "Bases". Tenant B: "Dairy", "Grains", "Produce" |
| **Packaging types, materials, colors** | Tenant A: "Drum 200L", "Steel", "Blue". Tenant B: "Crate 10kg", "Wood", "Natural" |
| **Warehouse bin types** | Tenant A: "Cold room rack", "Hazmat shelf". Tenant B: "Pallet bay", "Picking bin" |
| **Density parameters** | Industry-specific conversion factors per product |

Each tenant builds their own taxonomy from scratch. The system imposes no predefined categories.

### 2. JSONB Custom Attributes (schema-free extension)

`StockBatch` entities carry a `custom_attributes` JSONB column that accepts **arbitrary structured data per tenant** without schema migrations:

**Tenant A (Chemical distributor):**
```json
{
  "supplier": "Chem Corp Ltd",
  "hazmatCode": "UN1234",
  "certificationType": "ISO-14001",
  "temperatureControlled": true,
  "lotNumber": "LOT-2024-003456"
}
```

**Tenant B (Agricultural cooperative):**
```json
{
  "origin": "Mendoza, Argentina",
  "organic": true,
  "harvestDate": "2024-03-15",
  "moisture": 12.5,
  "grainType": "Triticum aestivum"
}
```

This is the most powerful customization mechanism: **unlimited tenant-specific fields with zero ALTER TABLE and zero code changes**. PostgreSQL JSONB supports indexing and querying these fields.

### 3. Warehouse Layout (custom depth)

Each tenant defines their own physical layout hierarchy:

```
Tenant A (simple):
  Warehouse "Main" → 3 bins

Tenant B (complex):
  Warehouse "Plant Norte" → Area "Cold Storage" → Location "Rack A" → Bin "A-01-03"
  Warehouse "Plant Norte" → Area "Dry Storage"  → Location "Floor 2"  → Bin "F2-15"
  Warehouse "Depot Sur"   → Area "Loading Dock" → ...
```

Warehouses, areas, locations, bins, and bin types are all tenant-scoped. Bin types (dimensions, capacity, constraints) are defined per tenant.

### 4. Pricing Models

Each tenant manages independent price lists with versioning:

- Multiple concurrent price lists (wholesale, retail, VIP, regional)
- Effective dates for version control (price changes without deleting history)
- Per-product price entries with quantity tiers
- Activate/deactivate lists without deletion

Tenant A may have 1 simple price list. Tenant B may have 15 lists covering different regions and customer tiers.

### 5. Cost Tracking

Costs are tracked per product or per batch, with tenant-defined cost types:

- Tenant A: "raw material", "transport", "storage"
- Tenant B: "seeds", "fertilizer", "labor", "packaging", "freight", "cold chain"

Currency is a string field — multi-currency is supported per transaction.

### 6. Workflow and Status Rules

Sale lifecycle follows a state machine (PENDING → COMPLETED / CANCELLED), but each transition is validated at the service layer. Stock batch statuses (ACTIVE, DEPLETED, EXPIRED, QUARANTINED) are enum-based but extensible.

### 7. Module Selection

Not every tenant needs every feature. Modules can be enabled/disabled:

```bash
# A small retailer needs only products, inventory, and users
./gradlew :demeter-app:quarkusDev -Pdemeter.modules=productos,inventario,usuarios

# A large manufacturer needs everything including AI
./gradlew :demeter-app:quarkusDev  # all modules by default
```

DLC modules (`fotos`, `chatbot`) are optional add-ons for tenants that need AI image processing or conversational interfaces.

### 8. Per-Tenant Unique Constraints

SKUs, sale numbers, batch codes, and user emails are unique **within a tenant**, not globally:

```sql
UNIQUE(tenant_id, sku)          -- Tenant A and B can both have SKU "PROD-001"
UNIQUE(tenant_id, sale_number)  -- Independent sale number sequences
UNIQUE(tenant_id, batch_code)   -- Independent batch codes
UNIQUE(tenant_id, email)        -- Same person can exist in multiple tenants
```

### Customization Summary

| Dimension | Freedom | Mechanism |
|-----------|---------|-----------|
| Product taxonomy | Unlimited | Tenant-scoped categories, families |
| Batch metadata | Unlimited | JSONB `custom_attributes` |
| Warehouse layout | Unlimited | Hierarchical entities, custom bin types |
| Pricing | Unlimited | Multiple versioned price lists |
| Cost types | Unlimited | Tenant-defined string types |
| Packaging catalogs | Unlimited | Tenant-scoped types, materials, colors |
| Units of measure | Unlimited | String field per record (kg, liters, boxes, pallets) |
| Currency | Unlimited | String field per transaction |
| Feature set | Configurable | Module selection at build/runtime |
| User roles | 3 levels | VIEWER, EDITOR, ADMIN (RBAC) |

---

## Quick Start

### Prerequisites

- Java 25 (SDKMAN: `sdk install java 25-open`)
- Docker (for PostgreSQL via Testcontainers/DevServices)

### Run in development

```bash
# All modules
./gradlew :demeter-app:quarkusDev

# Specific modules
./gradlew :demeter-app:quarkusDev -Pdemeter.modules=productos,inventario,ventas

# Check active modules
./gradlew :demeter-app:printDemeterModules
```

Quarkus DevServices automatically starts a PostgreSQL 17 container. No manual database setup needed.

### Run tests

```bash
./gradlew :demeter-app:test
```

42 integration tests covering:

| Suite | Tests | Coverage |
|-------|-------|---------|
| HealthCheckTest | 3 | Liveness, readiness, overall health |
| ProductControllerTest | 7 | CRUD + validation + 404 handling |
| StockBatchControllerTest | 10 | CRUD + filters by product/status + transitions |
| SaleFlowTest | 14 | Full lifecycle: product → batch → sale → complete/cancel |
| MultiTenantIsolationTest | 3 | Tenant A/B data isolation (products, batches, sales) |
| OpenApiTest | 5 | OpenAPI spec generation + Swagger UI |

### API documentation

With the app running:
- Swagger UI: `http://localhost:8080/q/swagger-ui/`
- OpenAPI spec: `http://localhost:8080/q/openapi`

### Example request

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "X-Tenant-ID: my-tenant" \
  -H "Content-Type: application/json" \
  -d '{"sku": "PROD-001", "name": "Widget Alpha", "description": "A test product"}'
```

---

## API Endpoints

| Module | Path | Methods |
|--------|------|---------|
| Productos | `/api/v1/products` | CRUD |
| Productos | `/api/v1/categories` | CRUD |
| Productos | `/api/v1/families` | CRUD |
| Inventario | `/api/v1/stock-batches` | CRUD + by-product, by-status |
| Inventario | `/api/v1/stock-movements` | CRUD + batch associations |
| Ventas | `/api/v1/sales` | CRUD + complete/cancel |
| Costos | `/api/v1/costs` | CRUD + by-product, by-batch |
| Precios | `/api/v1/price-lists` | CRUD + activate/deactivate |
| Precios | `/api/v1/price-lists/{id}/entries` | CRUD + bulk |
| Ubicaciones | `/api/v1/warehouses` | CRUD + soft delete |
| Ubicaciones | `/api/v1/storage-areas` | CRUD |
| Ubicaciones | `/api/v1/storage-locations` | CRUD |
| Ubicaciones | `/api/v1/storage-bins` | CRUD |
| Ubicaciones | `/api/v1/storage-bin-types` | CRUD |
| Empaquetado | `/api/v1/packaging-types` | CRUD |
| Empaquetado | `/api/v1/packaging-materials` | CRUD |
| Empaquetado | `/api/v1/packaging-colors` | CRUD |
| Empaquetado | `/api/v1/packaging-catalogs` | CRUD |
| Usuarios | `/api/v1/users` | CRUD + roles |
| Analytics | `/api/v1/analytics/*` | 7 read-only endpoints |
| Fotos | `/api/v1/photo-sessions` | CRUD + status polling |
| Fotos | `/api/v1/images` | Read + detections/classifications |
| Chatbot | `/api/v1/chat/sessions` | CRUD + messages |
| Chatbot | `/api/v1/chat/messages` | Read + tool executions |

---

## Production Deployment

### Authentication

Production uses OIDC (Auth0). The JWT token carries a `tenant_id` claim that the backend uses to resolve the tenant. Roles are mapped from the `permissions` claim.

```properties
%prod.quarkus.oidc.auth-server-url=${OIDC_AUTH_SERVER_URL}
%prod.quarkus.oidc.client-id=${OIDC_CLIENT_ID}
%prod.quarkus.oidc.token.issuer=${OIDC_ISSUER}
```

### Infrastructure

Included configurations for:
- **Docker**: Multi-stage Dockerfile + docker-compose (PostgreSQL 17 + pgAdmin)
- **Google Cloud**: Cloud Run service YAML, Cloud Build CI/CD, Terraform (Cloud Run + Cloud SQL)

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 25 |
| Framework | Quarkus 3.31.2 |
| ORM | Hibernate with Panache |
| Database | PostgreSQL 17 |
| Multi-Tenancy | Hibernate Discriminator + PostgreSQL RLS |
| Authentication | OIDC (Auth0) + JWT |
| Authorization | Jakarta `@RolesAllowed` (RBAC) |
| Migrations | Flyway (V1 baseline, V2 indexes, V3 RLS) |
| Build | Gradle 9.3.1 (multi-project) |
| Testing | JUnit 5 + REST Assured + Testcontainers |
| API Docs | SmallRye OpenAPI + Swagger UI |
