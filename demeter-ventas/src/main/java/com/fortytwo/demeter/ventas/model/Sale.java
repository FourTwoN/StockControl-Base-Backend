package com.fortytwo.demeter.ventas.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {

    @Column(name = "sale_number", nullable = false, length = 100)
    private String saleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SaleStatus status = SaleStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sold_by")
    private UUID soldBy;

    @Column(name = "sold_at", nullable = false)
    private Instant soldAt;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    // Getters
    public String getSaleNumber() { return saleNumber; }
    public SaleStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getNotes() { return notes; }
    public UUID getSoldBy() { return soldBy; }
    public Instant getSoldAt() { return soldAt; }
    public List<SaleItem> getItems() { return items; }

    // Setters
    public void setSaleNumber(String saleNumber) { this.saleNumber = saleNumber; }
    public void setStatus(SaleStatus status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setSoldBy(UUID soldBy) { this.soldBy = soldBy; }
    public void setSoldAt(Instant soldAt) { this.soldAt = soldAt; }
}
