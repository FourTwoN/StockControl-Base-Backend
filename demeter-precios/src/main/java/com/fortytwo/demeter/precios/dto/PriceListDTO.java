package com.fortytwo.demeter.precios.dto;

import com.fortytwo.demeter.precios.model.PriceList;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PriceListDTO(
    UUID id,
    String name,
    String description,
    LocalDate effectiveDate,
    boolean active,
    List<PriceEntryDTO> entries,
    Instant createdAt,
    Instant updatedAt
) {
    public static PriceListDTO from(PriceList pl) {
        List<PriceEntryDTO> entryDtos = pl.getEntries().stream()
                .map(PriceEntryDTO::from)
                .toList();
        return new PriceListDTO(
            pl.getId(),
            pl.getName(),
            pl.getDescription(),
            pl.getEffectiveDate(),
            pl.isActive(),
            entryDtos,
            pl.getCreatedAt(),
            pl.getUpdatedAt()
        );
    }
}
