package com.fortytwo.demeter.productos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "product_sample_images")
public class ProductSampleImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(length = 255)
    private String label;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    public Product getProduct() { return product; }
    public String getImageUrl() { return imageUrl; }
    public String getLabel() { return label; }
    public Integer getSortOrder() { return sortOrder; }

    public void setProduct(Product product) { this.product = product; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setLabel(String label) { this.label = label; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
