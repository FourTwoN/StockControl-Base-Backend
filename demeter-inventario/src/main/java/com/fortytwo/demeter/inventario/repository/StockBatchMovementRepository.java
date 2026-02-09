package com.fortytwo.demeter.inventario.repository;

import com.fortytwo.demeter.inventario.model.StockBatchMovement;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StockBatchMovementRepository implements PanacheRepositoryBase<StockBatchMovement, UUID> {

    public List<StockBatchMovement> findByBatchId(UUID batchId) {
        return find("batch.id", batchId).list();
    }

    public List<StockBatchMovement> findByMovementId(UUID movementId) {
        return find("movement.id", movementId).list();
    }
}
