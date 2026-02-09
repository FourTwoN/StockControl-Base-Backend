package com.fortytwo.demeter.ventas.dto;

import com.fortytwo.demeter.ventas.model.Sale;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleDTO(
    UUID id,
    String saleNumber,
    String status,
    BigDecimal totalAmount,
    String customerName,
    String customerEmail,
    String notes,
    UUID soldBy,
    Instant soldAt,
    List<SaleItemDTO> items,
    Instant createdAt,
    Instant updatedAt
) {
    public static SaleDTO from(Sale sale) {
        List<SaleItemDTO> itemDtos = sale.getItems() != null
            ? sale.getItems().stream().map(SaleItemDTO::from).toList()
            : List.of();

        return new SaleDTO(
            sale.getId(),
            sale.getSaleNumber(),
            sale.getStatus().name(),
            sale.getTotalAmount(),
            sale.getCustomerName(),
            sale.getCustomerEmail(),
            sale.getNotes(),
            sale.getSoldBy(),
            sale.getSoldAt(),
            itemDtos,
            sale.getCreatedAt(),
            sale.getUpdatedAt()
        );
    }
}
