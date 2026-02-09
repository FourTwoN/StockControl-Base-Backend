package com.fortytwo.demeter.precios.repository;

import com.fortytwo.demeter.precios.model.PriceList;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PriceListRepository implements PanacheRepositoryBase<PriceList, UUID> {

    public List<PriceList> findActive() {
        return find("active", true).list();
    }

    public List<PriceList> findByEffectiveDateBefore(LocalDate date) {
        return find("effectiveDate < ?1", date).list();
    }
}
