package com.fortytwo.demeter.ventas.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ventas.dto.*;
import com.fortytwo.demeter.ventas.model.Sale;
import com.fortytwo.demeter.ventas.model.SaleItem;
import com.fortytwo.demeter.ventas.model.SaleStatus;
import com.fortytwo.demeter.ventas.repository.SaleRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class SaleService {

    private static final Logger log = LoggerFactory.getLogger(SaleService.class);

    @Inject
    SaleRepository saleRepository;

    @Inject
    SaleCompletionService saleCompletionService;

    public PagedResponse<SaleDTO> findAll(int page, int size) {
        var query = saleRepository.findAll();
        var sales = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = sales.stream().map(SaleDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public SaleDTO findById(UUID id) {
        Sale sale = saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale", id));
        return SaleDTO.from(sale);
    }

    public List<SaleDTO> findByStatus(SaleStatus status) {
        return saleRepository.findByStatus(status).stream()
                .map(SaleDTO::from)
                .toList();
    }

    public List<SaleDTO> findByDateRange(Instant from, Instant to) {
        return saleRepository.findByDateRange(from, to).stream()
                .map(SaleDTO::from)
                .toList();
    }

    @Transactional
    public SaleDTO create(CreateSaleRequest request) {
        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber());
        sale.setStatus(SaleStatus.PENDING);
        sale.setCustomerName(request.customerName());
        sale.setCustomerEmail(request.customerEmail());
        sale.setNotes(request.notes());
        sale.setSoldBy(request.soldBy());
        sale.setSoldAt(Instant.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateSaleItemRequest itemRequest : request.items()) {
            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProductId(itemRequest.productId());
            item.setBatchId(itemRequest.batchId());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(itemRequest.unitPrice());

            BigDecimal subtotal = itemRequest.quantity()
                    .multiply(itemRequest.unitPrice())
                    .setScale(2, RoundingMode.HALF_UP);
            item.setSubtotal(subtotal);

            sale.getItems().add(item);
            totalAmount = totalAmount.add(subtotal);
        }

        sale.setTotalAmount(totalAmount);
        saleRepository.persist(sale);

        log.info("Sale created: {} with {} items, total: {}",
                sale.getSaleNumber(), sale.getItems().size(), sale.getTotalAmount());

        return SaleDTO.from(sale);
    }

    @Transactional
    public SaleDTO update(UUID id, UpdateSaleRequest request) {
        Sale sale = saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale", id));

        if (request.customerName() != null) sale.setCustomerName(request.customerName());
        if (request.customerEmail() != null) sale.setCustomerEmail(request.customerEmail());
        if (request.notes() != null) sale.setNotes(request.notes());

        return SaleDTO.from(sale);
    }

    @Transactional
    public SaleDTO completeSale(UUID id) {
        Sale sale = saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale", id));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException(
                    "Sale " + sale.getSaleNumber() + " cannot be completed. Current status: " + sale.getStatus());
        }

        sale.setStatus(SaleStatus.COMPLETED);
        saleCompletionService.processStockMovements(sale);

        log.info("Sale completed: {}", sale.getSaleNumber());
        return SaleDTO.from(sale);
    }

    @Transactional
    public SaleDTO cancelSale(UUID id) {
        Sale sale = saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale", id));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new IllegalStateException(
                    "Sale " + sale.getSaleNumber() + " cannot be cancelled. Current status: " + sale.getStatus());
        }

        sale.setStatus(SaleStatus.CANCELLED);

        log.info("Sale cancelled: {}", sale.getSaleNumber());
        return SaleDTO.from(sale);
    }

    @Transactional
    public void delete(UUID id) {
        Sale sale = saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale", id));
        saleRepository.delete(sale);
    }

    private String generateSaleNumber() {
        long timestamp = Instant.now().toEpochMilli();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SALE-" + timestamp + "-" + random;
    }
}
