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
@Table(name = "detections")
public class Detection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal confidence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "bounding_box", columnDefinition = "jsonb")
    private Map<String, Object> boundingBox;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public Image getImage() { return image; }
    public String getLabel() { return label; }
    public BigDecimal getConfidence() { return confidence; }
    public Map<String, Object> getBoundingBox() { return boundingBox; }
    public Instant getCreatedAt() { return createdAt; }

    // Setters
    public void setImage(Image image) { this.image = image; }
    public void setLabel(String label) { this.label = label; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public void setBoundingBox(Map<String, Object> boundingBox) { this.boundingBox = boundingBox; }

    // Only for testing / framework use
    protected void setId(UUID id) { this.id = id; }
}
