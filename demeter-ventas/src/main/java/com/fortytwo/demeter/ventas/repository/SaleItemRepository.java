package com.fortytwo.demeter.ventas.repository;

import com.fortytwo.demeter.ventas.model.SaleItem;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SaleItemRepository implements PanacheRepositoryBase<SaleItem, UUID> {

    public List<SaleItem> findBySaleId(UUID saleId) {
        return find("sale.id", saleId).list();
    }
}
