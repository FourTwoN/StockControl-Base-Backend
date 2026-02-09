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
@Table(name = "classifications")
public class Classification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private S3Image image;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 5, scale = 4)
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
    public S3Image getImage() { return image; }
    public String getCategory() { return category; }
    public BigDecimal getConfidence() { return confidence; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setImage(S3Image image) { this.image = image; }
    public void setCategory(String category) { this.category = category; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    // Only for testing / framework use
    protected void setId(UUID id) { this.id = id; }
}
