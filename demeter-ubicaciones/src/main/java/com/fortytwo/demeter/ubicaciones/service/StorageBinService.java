package com.fortytwo.demeter.ubicaciones.service;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.model.StorageBin;
import com.fortytwo.demeter.ubicaciones.repository.StorageBinRepository;
import com.fortytwo.demeter.ubicaciones.repository.StorageBinTypeRepository;
import com.fortytwo.demeter.ubicaciones.repository.StorageLocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StorageBinService {
    @Inject StorageBinRepository binRepository;
    @Inject StorageLocationRepository locationRepository;
    @Inject StorageBinTypeRepository binTypeRepository;

    public List<StorageBinDTO> findByLocation(UUID locationId) {
        return binRepository.findByLocation(locationId).stream().map(StorageBinDTO::from).toList();
    }

    public StorageBinDTO findById(UUID id) {
        return StorageBinDTO.from(binRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageBin", id)));
    }

    @Transactional
    public StorageBinDTO create(UUID locationId, CreateStorageBinRequest req) {
        var location = locationRepository.findByIdOptional(locationId)
            .orElseThrow(() -> new EntityNotFoundException("StorageLocation", locationId));
        StorageBin b = new StorageBin();
        b.setLocation(location);
        b.setCode(req.code());
        if (req.binTypeId() != null) {
            b.setBinType(binTypeRepository.findByIdOptional(req.binTypeId())
                .orElseThrow(() -> new EntityNotFoundException("StorageBinType", req.binTypeId())));
        }
        binRepository.persist(b);
        return StorageBinDTO.from(b);
    }

    @Transactional
    public void delete(UUID id) {
        StorageBin b = binRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageBin", id));
        b.setDeletedAt(Instant.now());
    }
}
