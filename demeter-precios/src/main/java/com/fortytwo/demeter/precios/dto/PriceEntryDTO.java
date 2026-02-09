package com.fortytwo.demeter.precios.dto;

import com.fortytwo.demeter.precios.model.PriceEntry;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PriceEntryDTO(
    UUID id,
    UUID priceListId,
    UUID productId,
    BigDecimal price,
    String currency,
    BigDecimal minQuantity,
    Instant createdAt,
    Instant updatedAt
) {
    public static PriceEntryDTO from(PriceEntry e) {
        return new PriceEntryDTO(
            e.getId(),
            e.getPriceList().getId(),
            e.getProductId(),
            e.getPrice(),
            e.getCurrency(),
            e.getMinQuantity(),
            e.getCreatedAt(),
            e.getUpdatedAt()
        );
    }
}
