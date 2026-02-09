package com.fortytwo.demeter.productos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_sizes")
public class ProductSize extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "min_value", precision = 10, scale = 2)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 10, scale = 2)
    private BigDecimal maxValue;

    @Column(length = 50)
    private String unit;

    public Product getProduct() { return product; }
    public String getLabel() { return label; }
    public BigDecimal getMinValue() { return minValue; }
    public BigDecimal getMaxValue() { return maxValue; }
    public String getUnit() { return unit; }

    public void setProduct(Product product) { this.product = product; }
    public void setLabel(String label) { this.label = label; }
    public void setMinValue(BigDecimal minValue) { this.minValue = minValue; }
    public void setMaxValue(BigDecimal maxValue) { this.maxValue = maxValue; }
    public void setUnit(String unit) { this.unit = unit; }
}
