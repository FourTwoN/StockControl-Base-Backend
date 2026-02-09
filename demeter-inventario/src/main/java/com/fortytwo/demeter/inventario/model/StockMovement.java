package com.fortytwo.demeter.inventario.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 50)
    private MovementType movementType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(length = 50)
    private String unit;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type", length = 100)
    private String referenceType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;

    @OneToMany(mappedBy = "movement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockBatchMovement> batchMovements = new ArrayList<>();

    // Getters
    public MovementType getMovementType() { return movementType; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public UUID getReferenceId() { return referenceId; }
    public String getReferenceType() { return referenceType; }
    public String getNotes() { return notes; }
    public UUID getPerformedBy() { return performedBy; }
    public Instant getPerformedAt() { return performedAt; }
    public List<StockBatchMovement> getBatchMovements() { return batchMovements; }

    // Setters
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setPerformedBy(UUID performedBy) { this.performedBy = performedBy; }
    public void setPerformedAt(Instant performedAt) { this.performedAt = performedAt; }
}
