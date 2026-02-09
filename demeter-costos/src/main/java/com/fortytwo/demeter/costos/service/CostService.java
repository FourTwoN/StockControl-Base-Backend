package com.fortytwo.demeter.costos.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.costos.dto.CostDTO;
import com.fortytwo.demeter.costos.dto.CreateCostRequest;
import com.fortytwo.demeter.costos.dto.UpdateCostRequest;
import com.fortytwo.demeter.costos.model.Cost;
import com.fortytwo.demeter.costos.repository.CostRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CostService {

    @Inject
    CostRepository costRepository;

    public PagedResponse<CostDTO> findAll(int page, int size) {
        var query = costRepository.findAll();
        var costs = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = costs.stream().map(CostDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public CostDTO findById(UUID id) {
        Cost cost = costRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Cost", id));
        return CostDTO.from(cost);
    }

    public List<CostDTO> findByProduct(UUID productId) {
        return costRepository.findByProductId(productId).stream()
                .map(CostDTO::from)
                .toList();
    }

    public List<CostDTO> findByBatch(UUID batchId) {
        return costRepository.findByBatchId(batchId).stream()
                .map(CostDTO::from)
                .toList();
    }

    @Transactional
    public CostDTO create(CreateCostRequest request) {
        Cost cost = new Cost();
        cost.setProductId(request.productId());
        cost.setBatchId(request.batchId());
        cost.setCostType(request.costType());
        cost.setAmount(request.amount());
        cost.setCurrency(request.currency() != null ? request.currency() : "USD");
        cost.setDescription(request.description());
        cost.setEffectiveDate(request.effectiveDate());

        costRepository.persist(cost);
        return CostDTO.from(cost);
    }

    @Transactional
    public CostDTO update(UUID id, UpdateCostRequest request) {
        Cost cost = costRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Cost", id));

        if (request.productId() != null) cost.setProductId(request.productId());
        if (request.batchId() != null) cost.setBatchId(request.batchId());
        if (request.costType() != null) cost.setCostType(request.costType());
        if (request.amount() != null) cost.setAmount(request.amount());
        if (request.currency() != null) cost.setCurrency(request.currency());
        if (request.description() != null) cost.setDescription(request.description());
        if (request.effectiveDate() != null) cost.setEffectiveDate(request.effectiveDate());

        return CostDTO.from(cost);
    }

    @Transactional
    public void delete(UUID id) {
        Cost cost = costRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Cost", id));
        costRepository.delete(cost);
    }
}
