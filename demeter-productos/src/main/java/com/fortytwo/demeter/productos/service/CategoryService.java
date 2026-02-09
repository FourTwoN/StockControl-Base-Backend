package com.fortytwo.demeter.productos.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.productos.dto.*;
import com.fortytwo.demeter.productos.model.ProductCategory;
import com.fortytwo.demeter.productos.repository.CategoryRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    public PagedResponse<CategoryDTO> findAll(int page, int size) {
        var query = categoryRepository.findAll();
        var categories = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = categories.stream().map(CategoryDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public CategoryDTO findById(UUID id) {
        ProductCategory cat = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
        return CategoryDTO.from(cat);
    }

    @Transactional
    public CategoryDTO create(CreateCategoryRequest request) {
        ProductCategory cat = new ProductCategory();
        cat.setName(request.name());
        cat.setDescription(request.description());
        if (request.parentId() != null) {
            cat.setParent(categoryRepository.findByIdOptional(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Category", request.parentId())));
        }
        categoryRepository.persist(cat);
        return CategoryDTO.from(cat);
    }

    @Transactional
    public CategoryDTO update(UUID id, CreateCategoryRequest request) {
        ProductCategory cat = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
        cat.setName(request.name());
        cat.setDescription(request.description());
        if (request.parentId() != null) {
            cat.setParent(categoryRepository.findByIdOptional(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Category", request.parentId())));
        } else {
            cat.setParent(null);
        }
        return CategoryDTO.from(cat);
    }

    @Transactional
    public void delete(UUID id) {
        ProductCategory cat = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));
        categoryRepository.delete(cat);
    }
}
