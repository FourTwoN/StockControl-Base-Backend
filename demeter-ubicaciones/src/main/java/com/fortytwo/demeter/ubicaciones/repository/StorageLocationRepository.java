package com.fortytwo.demeter.ubicaciones.repository;

import com.fortytwo.demeter.ubicaciones.model.StorageLocation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageLocationRepository implements PanacheRepositoryBase<StorageLocation, UUID> {
    public List<StorageLocation> findByArea(UUID areaId) {
        return find("area.id = ?1 AND deletedAt IS NULL", areaId).list();
    }
}
