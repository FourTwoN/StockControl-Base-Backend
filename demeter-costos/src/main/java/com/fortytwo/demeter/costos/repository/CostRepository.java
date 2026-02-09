package com.fortytwo.demeter.costos.repository;

import com.fortytwo.demeter.costos.model.Cost;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CostRepository implements PanacheRepositoryBase<Cost, UUID> {

    public List<Cost> findByProductId(UUID productId) {
        return find("productId", productId).list();
    }

    public List<Cost> findByBatchId(UUID batchId) {
        return find("batchId", batchId).list();
    }

    public List<Cost> findByCostType(String costType) {
        return find("costType", costType).list();
    }

    public List<Cost> findByDateRange(LocalDate from, LocalDate to) {
        return find("effectiveDate >= ?1 and effectiveDate <= ?2", from, to).list();
    }
}
