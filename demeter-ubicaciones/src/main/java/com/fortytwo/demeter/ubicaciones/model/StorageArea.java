package com.fortytwo.demeter.ubicaciones.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_areas")
public class StorageArea extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StorageLocation> locations = new ArrayList<>();

    // Getters
    public Warehouse getWarehouse() { return warehouse; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getDeletedAt() { return deletedAt; }
    public List<StorageLocation> getLocations() { return locations; }

    // Setters
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
