# Demeter AI 2.0 — Backend Progress

**Stack:** Java 25 + Quarkus 3.31.2 + Gradle 9.3.1 + PostgreSQL 17
**Package base:** `com.fortytwo.demeter.*`
**Modules:** 13 Gradle subprojects
**Total Java files:** 201

---

## Task Manager

### FASE 1: Scaffolding y Configuracion Base
- [x] Gradle setup (root build.gradle.kts, settings.gradle.kts, gradle.properties, wrapper)
- [x] 13 module build.gradle.kts files with dependency declarations
- [x] Common module (14 files): tenant resolver, RLS customizer, BaseEntity, auth, exceptions, DTOs
- [x] Database migrations: V1 baseline (30 tables), V2 tenant indexes, V3 RLS policies
- [x] application.properties (dev/staging/prod profiles, OIDC, Flyway, OpenAPI)
- [x] Docker: Dockerfile (multi-stage), docker-compose.yml (PostgreSQL 17 + pgAdmin)
- [x] Deploy: Cloud Run service YAML, Cloud Build CI/CD, Terraform (Cloud Run + Cloud SQL)
- [x] .gitignore, .dockerignore

### FASE 2: Core Modules — Datos Maestros
- [x] **demeter-productos** (23 files): Product, Category, Family, State, Size, SampleImage, DensityParameter
- [x] **demeter-ubicaciones** (30 files): Warehouse, StorageArea, StorageLocation, StorageBin, StorageBinType (soft delete)
- [x] **demeter-empaquetado** (24 files): PackagingCatalog, PackagingType, PackagingMaterial, PackagingColor
- [x] **demeter-usuarios** (8 files): User, UserRole, OIDC mapping

### FASE 3: Core Modules — Logica de Negocio
- [x] **demeter-inventario** (17 files): StockBatch, StockMovement, StockBatchMovement, BatchStatus, MovementType (ENTRADA/MUERTE/TRASPLANTE/VENTA/AJUSTE), JSONB custom_attributes
- [x] **demeter-ventas** (13 files): Sale, SaleItem, SaleStatus, SaleCompletionService (cross-module stock movements)
- [x] **demeter-costos** (7 files): Cost tracking with product/batch reference, cost types, date ranges
- [x] **demeter-precios** (13 files): PriceList with versioning (effectiveDate), PriceEntry, bulk add, activate/deactivate
- [x] **demeter-analytics** (11 files): 7 read-only analytics endpoints (stock summary, movements, inventory valuation, top products, location occupancy, dashboard, movement history)

### FASE 4: DLC Modules
- [x] **demeter-fotos** (23 files): PhotoProcessingSession, S3Image, Detection, Classification, Estimation, session status polling
- [x] **demeter-chatbot** (18 files): ChatSession, ChatMessage, ChatToolExecution, JSONB input/output, message roles

### FASE 5: Testing e Integracion
- [ ] Test configuration (application.properties for test profile)
- [ ] ProductControllerTest — CRUD integration tests
- [ ] MultiTenantIsolationTest — tenant A/B data isolation verification
- [ ] StockBatchControllerTest — batch CRUD + status transitions
- [ ] SaleFlowTest — end-to-end: product → batch → sale → stock movement
- [ ] HealthCheckTest — /q/health/live, /q/health/ready
- [ ] OpenApiTest — /q/openapi, Swagger UI

---

## Build Status

| Phase | Status | Build |
|-------|--------|-------|
| FASE 1 | DONE | BUILD SUCCESSFUL |
| FASE 2 | DONE | BUILD SUCCESSFUL |
| FASE 3 | DONE | BUILD SUCCESSFUL (2 cross-module fixes applied) |
| FASE 4 | DONE | BUILD SUCCESSFUL |
| FASE 5 | PENDING | — |

---

## Module Dependency Graph

```
demeter-app (Quarkus main)
├── demeter-analytics ── productos, inventario, ventas, costos, ubicaciones, empaquetado, precios
├── demeter-ventas ───── productos, inventario
├── demeter-costos ───── productos, inventario
├── demeter-precios ──── productos
├── demeter-inventario ─ productos
├── demeter-fotos ────── productos
├── demeter-chatbot ──── (common only)
├── demeter-productos ── (common only)
├── demeter-ubicaciones ─ (common only)
├── demeter-empaquetado ─ (common only)
├── demeter-usuarios ─── (common only)
└── demeter-common ───── (base module: BaseEntity, tenant, auth, exceptions)
```

---

## API Endpoints Summary

| Module | Base Path | Methods |
|--------|-----------|---------|
| Productos | `/api/v1/products` | CRUD |
| Productos | `/api/v1/categories` | CRUD |
| Productos | `/api/v1/families` | CRUD |
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
| Inventario | `/api/v1/stock-batches` | CRUD + filters |
| Inventario | `/api/v1/stock-movements` | CRUD + batch associations |
| Ventas | `/api/v1/sales` | CRUD + complete/cancel |
| Costos | `/api/v1/costs` | CRUD + by-product/by-batch |
| Precios | `/api/v1/price-lists` | CRUD + activate/deactivate |
| Precios | `/api/v1/price-lists/{id}/entries` | CRUD + bulk |
| Analytics | `/api/v1/analytics/*` | 7 read-only endpoints |
| Fotos | `/api/v1/photo-sessions` | CRUD + status polling |
| Fotos | `/api/v1/images` | read + detections/classifications |
| Chatbot | `/api/v1/chat/sessions` | CRUD + messages |
| Chatbot | `/api/v1/chat/messages` | read + tool executions |

---

## Issues Resolved During Build

1. **TenantResolver filename mismatch** — class `DemeterTenantResolver` was in file `TenantResolver.java`
2. **Deprecated config keys** — removed `quarkus.http.cors=true` and `quarkus.health.extensions.enabled`
3. **Java version upgrade** — changed from Java 21 to Java 25 (SDKMAN)
4. **Gradle version upgrade** — changed from 6.8.1 to 9.3.1 (SDKMAN)
5. **SaleCompletionService** — called non-existent `createSaleMovement()`, fixed to use `create(CreateStockMovementRequest)`
6. **AnalyticsService** — 8 type mismatches (String vs enum, missing getters), all fixed
