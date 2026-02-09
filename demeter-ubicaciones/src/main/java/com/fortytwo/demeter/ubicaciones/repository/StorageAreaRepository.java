package com.fortytwo.demeter.ubicaciones.repository;

import com.fortytwo.demeter.ubicaciones.model.StorageArea;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageAreaRepository implements PanacheRepositoryBase<StorageArea, UUID> {
    public List<StorageArea> findByWarehouse(UUID warehouseId) {
        return find("warehouse.id = ?1 AND deletedAt IS NULL", warehouseId).list();
    }
}
