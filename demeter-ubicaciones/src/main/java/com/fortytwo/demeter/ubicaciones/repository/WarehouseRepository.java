package com.fortytwo.demeter.ubicaciones.repository;

import com.fortytwo.demeter.ubicaciones.model.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WarehouseRepository implements PanacheRepositoryBase<Warehouse, UUID> {
    public List<Warehouse> findActive() {
        return find("deletedAt IS NULL").list();
    }
}
