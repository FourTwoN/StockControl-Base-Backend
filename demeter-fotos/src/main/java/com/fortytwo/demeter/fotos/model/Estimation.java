package com.fortytwo.demeter.fotos.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "estimations")
public class Estimation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PhotoProcessingSession session;

    @Column(name = "estimation_type", nullable = false, length = 100)
    private String estimationType;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal value;

    @Column(length = 50)
    private String unit;

    @Column(precision = 5, scale = 4)
    private BigDecimal confidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public PhotoProcessingSession getSession() { return session; }
    public String getEstimationType() { return estimationType; }
    public BigDecimal getValue() { return value; }
    public String getUnit() { return unit; }
    public BigDecimal getConfidence() { return confidence; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setSession(PhotoProcessingSession session) { this.session = session; }
    public void setEstimationType(String estimationType) { this.estimationType = estimationType; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    // Only for testing / framework use
    protected void setId(UUID id) { this.id = id; }
}
