package com.fortytwo.demeter.productos.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.productos.dto.*;
import com.fortytwo.demeter.productos.model.ProductFamily;
import com.fortytwo.demeter.productos.repository.FamilyRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class ProductFamilyService {

    @Inject
    FamilyRepository familyRepository;

    public PagedResponse<FamilyDTO> findAll(int page, int size) {
        var query = familyRepository.findAll();
        var families = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = families.stream().map(FamilyDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public FamilyDTO findById(UUID id) {
        ProductFamily family = familyRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Family", id));
        return FamilyDTO.from(family);
    }

    @Transactional
    public FamilyDTO create(CreateFamilyRequest request) {
        ProductFamily family = new ProductFamily();
        family.setName(request.name());
        family.setDescription(request.description());
        familyRepository.persist(family);
        return FamilyDTO.from(family);
    }

    @Transactional
    public FamilyDTO update(UUID id, CreateFamilyRequest request) {
        ProductFamily family = familyRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Family", id));
        family.setName(request.name());
        family.setDescription(request.description());
        return FamilyDTO.from(family);
    }

    @Transactional
    public void delete(UUID id) {
        ProductFamily family = familyRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Family", id));
        familyRepository.delete(family);
    }
}
