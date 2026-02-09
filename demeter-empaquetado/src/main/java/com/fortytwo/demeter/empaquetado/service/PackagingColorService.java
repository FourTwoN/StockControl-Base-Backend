package com.fortytwo.demeter.empaquetado.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.empaquetado.dto.*;
import com.fortytwo.demeter.empaquetado.model.PackagingColor;
import com.fortytwo.demeter.empaquetado.repository.PackagingColorRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class PackagingColorService {
    @Inject PackagingColorRepository repository;

    public PagedResponse<PackagingColorDTO> findAll(int page, int size) {
        var query = repository.findAll();
        var list = query.page(Page.of(page, size)).list();
        return PagedResponse.of(list.stream().map(PackagingColorDTO::from).toList(), page, size, query.count());
    }

    public PackagingColorDTO findById(UUID id) {
        return PackagingColorDTO.from(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingColor", id)));
    }

    @Transactional
    public PackagingColorDTO create(CreatePackagingColorRequest req) {
        PackagingColor c = new PackagingColor();
        c.setName(req.name());
        c.setHexCode(req.hexCode());
        repository.persist(c);
        return PackagingColorDTO.from(c);
    }

    @Transactional
    public PackagingColorDTO update(UUID id, CreatePackagingColorRequest req) {
        PackagingColor c = repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingColor", id));
        c.setName(req.name());
        c.setHexCode(req.hexCode());
        return PackagingColorDTO.from(c);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(repository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("PackagingColor", id)));
    }
}
