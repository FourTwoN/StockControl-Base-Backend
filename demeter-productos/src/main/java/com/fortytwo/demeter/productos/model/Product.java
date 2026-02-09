package com.fortytwo.demeter.productos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private ProductFamily family;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProductState state = ProductState.ACTIVE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> sizes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSampleImage> sampleImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DensityParameter> densityParameters = new ArrayList<>();

    // Getters
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProductCategory getCategory() { return category; }
    public ProductFamily getFamily() { return family; }
    public ProductState getState() { return state; }
    public List<ProductSize> getSizes() { return sizes; }
    public List<ProductSampleImage> getSampleImages() { return sampleImages; }
    public List<DensityParameter> getDensityParameters() { return densityParameters; }

    // Setters
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public void setFamily(ProductFamily family) { this.family = family; }
    public void setState(ProductState state) { this.state = state; }
}
