package com.fortytwo.demeter.costos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "costs")
public class Cost extends BaseEntity {

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "cost_type", nullable = false, length = 100)
    private String costType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    // Getters
    public UUID getProductId() { return productId; }
    public UUID getBatchId() { return batchId; }
    public String getCostType() { return costType; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public LocalDate getEffectiveDate() { return effectiveDate; }

    // Setters
    public void setProductId(UUID productId) { this.productId = productId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public void setCostType(String costType) { this.costType = costType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setDescription(String description) { this.description = description; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
}
