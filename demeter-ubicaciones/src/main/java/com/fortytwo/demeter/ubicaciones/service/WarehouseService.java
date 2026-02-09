package com.fortytwo.demeter.ubicaciones.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.model.Warehouse;
import com.fortytwo.demeter.ubicaciones.repository.WarehouseRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class WarehouseService {
    @Inject WarehouseRepository warehouseRepository;

    public PagedResponse<WarehouseDTO> findAll(int page, int size) {
        var query = warehouseRepository.find("deletedAt IS NULL");
        var list = query.page(Page.of(page, size)).list();
        long total = query.count();
        return PagedResponse.of(list.stream().map(WarehouseDTO::from).toList(), page, size, total);
    }

    public WarehouseDTO findById(UUID id) {
        return WarehouseDTO.from(warehouseRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", id)));
    }

    @Transactional
    public WarehouseDTO create(CreateWarehouseRequest req) {
        Warehouse w = new Warehouse();
        w.setName(req.name());
        w.setAddress(req.address());
        w.setLatitude(req.latitude());
        w.setLongitude(req.longitude());
        warehouseRepository.persist(w);
        return WarehouseDTO.from(w);
    }

    @Transactional
    public WarehouseDTO update(UUID id, CreateWarehouseRequest req) {
        Warehouse w = warehouseRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", id));
        w.setName(req.name());
        w.setAddress(req.address());
        w.setLatitude(req.latitude());
        w.setLongitude(req.longitude());
        return WarehouseDTO.from(w);
    }

    @Transactional
    public void delete(UUID id) {
        Warehouse w = warehouseRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", id));
        w.setDeletedAt(Instant.now());
        w.setActive(false);
    }
}
