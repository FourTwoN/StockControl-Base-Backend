package com.fortytwo.demeter.empaquetado.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "packaging_types")
public class PackagingType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}
