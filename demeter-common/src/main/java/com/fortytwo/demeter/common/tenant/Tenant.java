package com.fortytwo.demeter.common.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @Size(min = 2, max = 64)
    private String id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String industry;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> theme = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "enabled_modules", columnDefinition = "jsonb", nullable = false)
    private List<String> enabledModules = List.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> settings = Map.of();

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getIndustry() { return industry; }
    public Map<String, Object> getTheme() { return theme; }
    public List<String> getEnabledModules() { return enabledModules; }
    public Map<String, Object> getSettings() { return settings; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setTheme(Map<String, Object> theme) { this.theme = theme; }
    public void setEnabledModules(List<String> enabledModules) { this.enabledModules = enabledModules; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
    public void setActive(boolean active) { this.active = active; }
}
