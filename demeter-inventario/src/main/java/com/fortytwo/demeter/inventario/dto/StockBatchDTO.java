package com.fortytwo.demeter.inventario.dto;

import com.fortytwo.demeter.inventario.model.StockBatch;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record StockBatchDTO(
    UUID id,
    UUID productId,
    String productName,
    String batchCode,
    BigDecimal quantity,
    String unit,
    UUID warehouseId,
    UUID binId,
    String status,
    Map<String, Object> customAttributes,
    Instant entryDate,
    Instant expiryDate,
    Instant createdAt,
    Instant updatedAt
) {
    public static StockBatchDTO from(StockBatch b) {
        return new StockBatchDTO(
            b.getId(),
            b.getProduct() != null ? b.getProduct().getId() : null,
            b.getProduct() != null ? b.getProduct().getName() : null,
            b.getBatchCode(),
            b.getQuantity(),
            b.getUnit(),
            b.getWarehouseId(),
            b.getBinId(),
            b.getStatus().name(),
            b.getCustomAttributes(),
            b.getEntryDate(),
            b.getExpiryDate(),
            b.getCreatedAt(),
            b.getUpdatedAt()
        );
    }
}
