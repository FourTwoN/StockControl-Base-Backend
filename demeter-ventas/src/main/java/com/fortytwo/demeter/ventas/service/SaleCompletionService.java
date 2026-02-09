package com.fortytwo.demeter.ventas.service;

import com.fortytwo.demeter.inventario.dto.CreateStockMovementRequest;
import com.fortytwo.demeter.inventario.service.StockMovementService;
import com.fortytwo.demeter.ventas.model.Sale;
import com.fortytwo.demeter.ventas.model.SaleItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class SaleCompletionService {

    private static final Logger log = LoggerFactory.getLogger(SaleCompletionService.class);

    @Inject
    StockMovementService stockMovementService;

    public void processStockMovements(Sale sale) {
        log.info("Processing stock movements for sale: {}", sale.getSaleNumber());

        for (SaleItem item : sale.getItems()) {
            if (item.getBatchId() == null) {
                log.warn("Sale item {} has no batch assigned, skipping stock movement", item.getId());
                continue;
            }

            var batchQuantity = new CreateStockMovementRequest.BatchQuantity(
                    item.getBatchId(),
                    item.getQuantity()
            );

            var request = new CreateStockMovementRequest(
                    "VENTA",
                    item.getQuantity(),
                    null,
                    sale.getId(),
                    "SALE",
                    "Sale " + sale.getSaleNumber(),
                    sale.getSoldBy(),
                    Instant.now(),
                    List.of(batchQuantity)
            );

            stockMovementService.create(request);
            log.debug("Stock movement created for product {} batch {} in sale {}",
                    item.getProductId(), item.getBatchId(), sale.getSaleNumber());
        }

        log.info("All stock movements processed for sale: {}", sale.getSaleNumber());
    }
}
