package com.fortytwo.demeter.ubicaciones.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.ubicaciones.dto.*;
import com.fortytwo.demeter.ubicaciones.model.StorageBinType;
import com.fortytwo.demeter.ubicaciones.repository.StorageBinTypeRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class StorageBinTypeService {
    @Inject StorageBinTypeRepository repository;

    public PagedResponse<StorageBinTypeDTO> findAll(int page, int size) {
        var query = repository.findAll();
        var list = query.page(Page.of(page, size)).list();
        long total = query.count();
        return PagedResponse.of(list.stream().map(StorageBinTypeDTO::from).toList(), page, size, total);
    }

    public StorageBinTypeDTO findById(UUID id) {
        return StorageBinTypeDTO.from(repository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageBinType", id)));
    }

    @Transactional
    public StorageBinTypeDTO create(CreateStorageBinTypeRequest req) {
        StorageBinType t = new StorageBinType();
        t.setName(req.name());
        t.setCapacity(req.capacity());
        t.setDescription(req.description());
        repository.persist(t);
        return StorageBinTypeDTO.from(t);
    }

    @Transactional
    public void delete(UUID id) {
        StorageBinType t = repository.findByIdOptional(id)
            .orElseThrow(() -> new EntityNotFoundException("StorageBinType", id));
        repository.delete(t);
    }
}
