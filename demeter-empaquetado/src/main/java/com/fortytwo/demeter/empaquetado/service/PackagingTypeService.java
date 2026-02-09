package com.fortytwo.demeter.empaquetado.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.empaquetado.dto.*;
import com.fortytwo.demeter.empaquetado.model.PackagingType;
import com.fortytwo.demeter.empaquetado.repository.PackagingTypeRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class PackagingTypeService {
    @Inject PackagingTypeRepository repository;

    public PagedResponse<PackagingTypeDTO> findAll(int page, int size) {
        var query = repository.findAll();
        var list = query.page(Page.of(page, size)).list();
        return PagedResponse.of(list.stream().map(PackagingTypeDTO::from).toList(), page, size, query.count());
    }

    public PackagingTypeDTO findById(UUID id) {
        return PackagingTypeDTO.from(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingType", id)));
    }

    @Transactional
    public PackagingTypeDTO create(CreatePackagingTypeRequest req) {
        PackagingType t = new PackagingType();
        t.setName(req.name());
        t.setDescription(req.description());
        repository.persist(t);
        return PackagingTypeDTO.from(t);
    }

    @Transactional
    public PackagingTypeDTO update(UUID id, CreatePackagingTypeRequest req) {
        PackagingType t = repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingType", id));
        t.setName(req.name());
        t.setDescription(req.description());
        return PackagingTypeDTO.from(t);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingType", id)));
    }
}
