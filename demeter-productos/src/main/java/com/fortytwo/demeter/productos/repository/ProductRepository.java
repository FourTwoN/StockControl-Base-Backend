package com.fortytwo.demeter.productos.repository;

import com.fortytwo.demeter.productos.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {

    public Optional<Product> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }

    public List<Product> findByCategory(UUID categoryId) {
        return find("category.id", categoryId).list();
    }

    public List<Product> findByFamily(UUID familyId) {
        return find("family.id", familyId).list();
    }
}
