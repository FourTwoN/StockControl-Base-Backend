package com.fortytwo.demeter.inventario.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_batch_movements")
public class StockBatchMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private StockBatch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_id", nullable = false)
    private StockMovement movement;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public StockBatch getBatch() { return batch; }
    public StockMovement getMovement() { return movement; }
    public BigDecimal getQuantity() { return quantity; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setBatch(StockBatch batch) { this.batch = batch; }
    public void setMovement(StockMovement movement) { this.movement = movement; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
