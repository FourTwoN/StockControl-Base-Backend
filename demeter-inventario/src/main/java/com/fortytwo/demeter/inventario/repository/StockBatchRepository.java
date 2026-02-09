package com.fortytwo.demeter.inventario.repository;

import com.fortytwo.demeter.inventario.model.BatchStatus;
import com.fortytwo.demeter.inventario.model.StockBatch;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StockBatchRepository implements PanacheRepositoryBase<StockBatch, UUID> {

    public Optional<StockBatch> findByBatchCode(String code) {
        return find("batchCode", code).firstResultOptional();
    }

    public List<StockBatch> findByProductId(UUID productId) {
        return find("product.id", productId).list();
    }

    public List<StockBatch> findByWarehouseId(UUID warehouseId) {
        return find("warehouseId", warehouseId).list();
    }

    public List<StockBatch> findByStatus(BatchStatus status) {
        return find("status", status).list();
    }
}
