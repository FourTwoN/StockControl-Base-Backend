package com.fortytwo.demeter.precios.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_entries")
public class PriceEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "min_quantity", precision = 12, scale = 2)
    private BigDecimal minQuantity = BigDecimal.ONE;

    // Getters
    public PriceList getPriceList() { return priceList; }
    public UUID getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public BigDecimal getMinQuantity() { return minQuantity; }

    // Setters
    public void setPriceList(PriceList priceList) { this.priceList = priceList; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setMinQuantity(BigDecimal minQuantity) { this.minQuantity = minQuantity; }
}
