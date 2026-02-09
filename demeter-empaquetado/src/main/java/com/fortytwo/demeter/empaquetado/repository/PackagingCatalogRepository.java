package com.fortytwo.demeter.empaquetado.repository;

import com.fortytwo.demeter.empaquetado.model.PackagingCatalog;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PackagingCatalogRepository implements PanacheRepositoryBase<PackagingCatalog, UUID> {
    public List<PackagingCatalog> findByType(UUID typeId) {
        return find("type.id", typeId).list();
    }
}
