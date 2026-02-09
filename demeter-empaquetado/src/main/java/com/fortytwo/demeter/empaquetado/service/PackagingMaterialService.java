package com.fortytwo.demeter.empaquetado.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.empaquetado.dto.*;
import com.fortytwo.demeter.empaquetado.model.PackagingMaterial;
import com.fortytwo.demeter.empaquetado.repository.PackagingMaterialRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class PackagingMaterialService {
    @Inject PackagingMaterialRepository repository;

    public PagedResponse<PackagingMaterialDTO> findAll(int page, int size) {
        var query = repository.findAll();
        var list = query.page(Page.of(page, size)).list();
        return PagedResponse.of(list.stream().map(PackagingMaterialDTO::from).toList(), page, size, query.count());
    }

    public PackagingMaterialDTO findById(UUID id) {
        return PackagingMaterialDTO.from(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingMaterial", id)));
    }

    @Transactional
    public PackagingMaterialDTO create(CreatePackagingMaterialRequest req) {
        PackagingMaterial m = new PackagingMaterial();
        m.setName(req.name());
        m.setDescription(req.description());
        repository.persist(m);
        return PackagingMaterialDTO.from(m);
    }

    @Transactional
    public PackagingMaterialDTO update(UUID id, CreatePackagingMaterialRequest req) {
        PackagingMaterial m = repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingMaterial", id));
        m.setName(req.name());
        m.setDescription(req.description());
        return PackagingMaterialDTO.from(m);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingMaterial", id)));
    }
}
