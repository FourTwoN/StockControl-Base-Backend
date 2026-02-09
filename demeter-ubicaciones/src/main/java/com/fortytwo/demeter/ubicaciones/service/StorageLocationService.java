package com.fortytwo.demeter.ubicaciones.service;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.model.StorageLocation;
import com.fortytwo.demeter.ubicaciones.repository.StorageAreaRepository;
import com.fortytwo.demeter.ubicaciones.repository.StorageLocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageLocationService {
    @Inject StorageLocationRepository locationRepository;
    @Inject StorageAreaRepository areaRepository;

    public List<StorageLocationDTO> findByArea(UUID areaId) {
        return locationRepository.findByArea(areaId).stream().map(StorageLocationDTO::from).toList();
    }

    public StorageLocationDTO findById(UUID id) {
        return StorageLocationDTO.from(locationRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageLocation", id)));
    }

    @Transactional
    public StorageLocationDTO create(UUID areaId, CreateStorageLocationRequest req) {
        var area = areaRepository.findByIdOptional(areaId)
            .orElseThrow(() -> new EntityNotFoundException("StorageArea", areaId));
        StorageLocation l = new StorageLocation();
        l.setArea(area);
        l.setName(req.name());
        l.setDescription(req.description());
        locationRepository.persist(l);
        return StorageLocationDTO.from(l);
    }

    @Transactional
    public StorageLocationDTO update(UUID id, CreateStorageLocationRequest req) {
        StorageLocation l = locationRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageLocation", id));
        l.setName(req.name());
        l.setDescription(req.description());
        return StorageLocationDTO.from(l);
    }

    @Transactional
    public void delete(UUID id) {
        StorageLocation l = locationRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageLocation", id));
        l.setDeletedAt(Instant.now());
    }
}
