package com.fortytwo.demeter.productos.repository;

import com.fortytwo.demeter.productos.model.ProductCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository implements PanacheRepositoryBase<ProductCategory, UUID> {

    public List<ProductCategory> findRootCategories() {
        return find("parent IS NULL").list();
    }

    public List<ProductCategory> findByParent(UUID parentId) {
        return find("parent.id", parentId).list();
    }
}
