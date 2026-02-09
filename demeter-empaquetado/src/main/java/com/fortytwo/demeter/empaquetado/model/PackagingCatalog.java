package com.fortytwo.demeter.empaquetado.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "packaging_catalogs")
public class PackagingCatalog extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private PackagingType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private PackagingMaterial material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private PackagingColor color;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(length = 50)
    private String unit;

    public String getName() { return name; }
    public PackagingType getType() { return type; }
    public PackagingMaterial getMaterial() { return material; }
    public PackagingColor getColor() { return color; }
    public BigDecimal getCapacity() { return capacity; }
    public String getUnit() { return unit; }

    public void setName(String name) { this.name = name; }
    public void setType(PackagingType type) { this.type = type; }
    public void setMaterial(PackagingMaterial material) { this.material = material; }
    public void setColor(PackagingColor color) { this.color = color; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }
    public void setUnit(String unit) { this.unit = unit; }
}
