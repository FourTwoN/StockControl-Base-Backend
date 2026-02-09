package com.fortytwo.demeter.precios.repository;

import com.fortytwo.demeter.precios.model.PriceEntry;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PriceEntryRepository implements PanacheRepositoryBase<PriceEntry, UUID> {

    public List<PriceEntry> findByPriceListId(UUID priceListId) {
        return find("priceList.id", priceListId).list();
    }

    public List<PriceEntry> findByProductId(UUID productId) {
        return find("productId", productId).list();
    }

    public Optional<PriceEntry> findCurrentPriceForProduct(UUID productId) {
        return find(
            "productId = ?1 and priceList.active = true order by priceList.effectiveDate desc",
            productId
        ).firstResultOptional();
    }
}
