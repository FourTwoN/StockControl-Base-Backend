package com.fortytwo.demeter.ubicaciones.repository;

import com.fortytwo.demeter.ubicaciones.model.StorageBin;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageBinRepository implements PanacheRepositoryBase<StorageBin, UUID> {
    public List<StorageBin> findByLocation(UUID locationId) {
        return find("location.id = ?1 AND deletedAt IS NULL", locationId).list();
    }
    public List<StorageBin> findAvailable() {
        return find("occupied = false AND deletedAt IS NULL").list();
    }
}
