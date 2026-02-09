package com.fortytwo.demeter.productos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "product_categories")
public class ProductCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategory parent;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProductCategory getParent() { return parent; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setParent(ProductCategory parent) { this.parent = parent; }
}
