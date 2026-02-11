package com.fortytwo.demeter.fotos.model;

import com.fortytwo.demeter.common.model.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "photo_processing_sessions")
public class PhotoProcessingSession extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Column(name = "total_images", nullable = false)
    private int totalImages;

    @Column(name = "processed_images", nullable = false)
    private int processedImages;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estimation> estimations = new ArrayList<>();

    // Getters
    public ProcessingStatus getStatus() { return status; }
    public UUID getProductId() { return productId; }
    public UUID getBatchId() { return batchId; }
    public UUID getUploadedBy() { return uploadedBy; }
    public int getTotalImages() { return totalImages; }
    public int getProcessedImages() { return processedImages; }
    public List<Image> getImages() { return images; }
    public List<Estimation> getEstimations() { return estimations; }

    // Setters
    public void setStatus(ProcessingStatus status) { this.status = status; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public void setUploadedBy(UUID uploadedBy) { this.uploadedBy = uploadedBy; }
    public void setTotalImages(int totalImages) { this.totalImages = totalImages; }
    public void setProcessedImages(int processedImages) { this.processedImages = processedImages; }
}
