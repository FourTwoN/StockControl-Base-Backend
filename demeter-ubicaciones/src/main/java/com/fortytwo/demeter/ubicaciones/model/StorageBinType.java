package com.fortytwo.demeter.ubicaciones.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "storage_bin_types")
public class StorageBinType extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private Integer capacity;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Getters
    public String getName() { return name; }
    public Integer getCapacity() { return capacity; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public void setDescription(String description) { this.description = description; }
}
