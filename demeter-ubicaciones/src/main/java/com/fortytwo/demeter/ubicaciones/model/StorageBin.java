package com.fortytwo.demeter.ubicaciones.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "storage_bins")
public class StorageBin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private StorageLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_type_id")
    private StorageBinType binType;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false)
    private boolean occupied = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Getters
    public StorageLocation getLocation() { return location; }
    public StorageBinType getBinType() { return binType; }
    public String getCode() { return code; }
    public boolean isOccupied() { return occupied; }
    public Instant getDeletedAt() { return deletedAt; }

    // Setters
    public void setLocation(StorageLocation location) { this.location = location; }
    public void setBinType(StorageBinType binType) { this.binType = binType; }
    public void setCode(String code) { this.code = code; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
