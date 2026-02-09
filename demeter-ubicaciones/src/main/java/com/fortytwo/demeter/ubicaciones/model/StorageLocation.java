package com.fortytwo.demeter.ubicaciones.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_locations")
public class StorageLocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private StorageArea area;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StorageBin> bins = new ArrayList<>();

    // Getters
    public StorageArea getArea() { return area; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getDeletedAt() { return deletedAt; }
    public List<StorageBin> getBins() { return bins; }

    // Setters
    public void setArea(StorageArea area) { this.area = area; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
