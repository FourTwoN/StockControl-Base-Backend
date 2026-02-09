package com.fortytwo.demeter.ubicaciones.service;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.model.StorageArea;
import com.fortytwo.demeter.ubicaciones.repository.StorageAreaRepository;
import com.fortytwo.demeter.ubicaciones.repository.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageAreaService {
    @Inject StorageAreaRepository areaRepository;
    @Inject WarehouseRepository warehouseRepository;

    public List<StorageAreaDTO> findByWarehouse(UUID warehouseId) {
        return areaRepository.findByWarehouse(warehouseId).stream().map(StorageAreaDTO::from).toList();
    }

    public StorageAreaDTO findById(UUID id) {
        return StorageAreaDTO.from(areaRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageArea", id)));
    }

    @Transactional
    public StorageAreaDTO create(UUID warehouseId, CreateStorageAreaRequest req) {
        var warehouse = warehouseRepository.findByIdOptional(warehouseId)
            .orElseThrow(() -> new EntityNotFoundException("Warehouse", warehouseId));
        StorageArea a = new StorageArea();
        a.setWarehouse(warehouse);
        a.setName(req.name());
        a.setDescription(req.description());
        areaRepository.persist(a);
        return StorageAreaDTO.from(a);
    }

    @Transactional
    public StorageAreaDTO update(UUID id, CreateStorageAreaRequest req) {
        StorageArea a = areaRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageArea", id));
        a.setName(req.name());
        a.setDescription(req.description());
        return StorageAreaDTO.from(a);
    }

    @Transactional
    public void delete(UUID id) {
        StorageArea a = areaRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageArea", id));
        a.setDeletedAt(Instant.now());
    }
}
