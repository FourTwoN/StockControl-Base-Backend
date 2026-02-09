package com.fortytwo.demeter.productos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "density_parameters")
public class DensityParameter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "parameter_name", nullable = false)
    private String parameterName;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    @Column(length = 50)
    private String unit;

    public Product getProduct() { return product; }
    public String getParameterName() { return parameterName; }
    public BigDecimal getValue() { return value; }
    public String getUnit() { return unit; }

    public void setProduct(Product product) { this.product = product; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setUnit(String unit) { this.unit = unit; }
}
