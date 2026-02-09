package com.fortytwo.demeter.inventario.dto;

import com.fortytwo.demeter.inventario.model.StockBatchMovement;
import com.fortytwo.demeter.inventario.model.StockMovement;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockMovementDTO(
    UUID id,
    String movementType,
    BigDecimal quantity,
    String unit,
    UUID referenceId,
    String referenceType,
    String notes,
    UUID performedBy,
    Instant performedAt,
    List<BatchMovementDetail> batchMovements,
    Instant createdAt,
    Instant updatedAt
) {
    public record BatchMovementDetail(
        UUID id,
        UUID batchId,
        String batchCode,
        BigDecimal quantity,
        Instant createdAt
    ) {
        public static BatchMovementDetail from(StockBatchMovement bm) {
            return new BatchMovementDetail(
                bm.getId(),
                bm.getBatch() != null ? bm.getBatch().getId() : null,
                bm.getBatch() != null ? bm.getBatch().getBatchCode() : null,
                bm.getQuantity(),
                bm.getCreatedAt()
            );
        }
    }

    public static StockMovementDTO from(StockMovement m) {
        List<BatchMovementDetail> details = m.getBatchMovements() != null
            ? m.getBatchMovements().stream().map(BatchMovementDetail::from).toList()
            : List.of();

        return new StockMovementDTO(
            m.getId(),
            m.getMovementType().name(),
            m.getQuantity(),
            m.getUnit(),
            m.getReferenceId(),
            m.getReferenceType(),
            m.getNotes(),
            m.getPerformedBy(),
            m.getPerformedAt(),
            details,
            m.getCreatedAt(),
            m.getUpdatedAt()
        );
    }
}
