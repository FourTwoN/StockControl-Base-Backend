package com.fortytwo.demeter.precios.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "price_lists")
public class PriceList extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "priceList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceEntry> entries = new ArrayList<>();

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public boolean isActive() { return active; }
    public List<PriceEntry> getEntries() { return entries; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public void setActive(boolean active) { this.active = active; }
}
