package com.fortytwo.demeter.ventas.dto;

import com.fortytwo.demeter.ventas.model.SaleItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaleItemDTO(
    UUID id,
    UUID saleId,
    UUID productId,
    UUID batchId,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal,
    Instant createdAt,
    Instant updatedAt
) {
    public static SaleItemDTO from(SaleItem item) {
        return new SaleItemDTO(
            item.getId(),
            item.getSale() != null ? item.getSale().getId() : null,
            item.getProductId(),
            item.getBatchId(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}
