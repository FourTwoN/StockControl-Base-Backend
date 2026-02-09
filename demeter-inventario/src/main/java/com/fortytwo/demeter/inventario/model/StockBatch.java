package com.fortytwo.demeter.inventario.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import com.fortytwo.demeter.productos.model.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "stock_batches")
public class StockBatch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "batch_code", nullable = false, length = 100)
    private String batchCode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(length = 50)
    private String unit;

    @Column(name = "warehouse_id")
    private UUID warehouseId;

    @Column(name = "bin_id")
    private UUID binId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BatchStatus status = BatchStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_attributes", columnDefinition = "jsonb")
    private Map<String, Object> customAttributes;

    @Column(name = "entry_date", nullable = false)
    private Instant entryDate;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    // Getters
    public Product getProduct() { return product; }
    public String getBatchCode() { return batchCode; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public UUID getWarehouseId() { return warehouseId; }
    public UUID getBinId() { return binId; }
    public BatchStatus getStatus() { return status; }
    public Map<String, Object> getCustomAttributes() { return customAttributes; }
    public Instant getEntryDate() { return entryDate; }
    public Instant getExpiryDate() { return expiryDate; }

    // Setters
    public void setProduct(Product product) { this.product = product; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
    public void setBinId(UUID binId) { this.binId = binId; }
    public void setStatus(BatchStatus status) { this.status = status; }
    public void setCustomAttributes(Map<String, Object> customAttributes) { this.customAttributes = customAttributes; }
    public void setEntryDate(Instant entryDate) { this.entryDate = entryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
}
