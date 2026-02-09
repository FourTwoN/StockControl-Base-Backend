package com.fortytwo.demeter.ventas.repository;

import com.fortytwo.demeter.ventas.model.Sale;
import com.fortytwo.demeter.ventas.model.SaleStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SaleRepository implements PanacheRepositoryBase<Sale, UUID> {

    public Optional<Sale> findBySaleNumber(String saleNumber) {
        return find("saleNumber", saleNumber).firstResultOptional();
    }

    public List<Sale> findByStatus(SaleStatus status) {
        return find("status", status).list();
    }

    public List<Sale> findByDateRange(Instant from, Instant to) {
        return find("soldAt >= ?1 and soldAt <= ?2", from, to).list();
    }
}
