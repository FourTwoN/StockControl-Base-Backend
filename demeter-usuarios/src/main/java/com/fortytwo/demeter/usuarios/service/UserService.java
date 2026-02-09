package com.fortytwo.demeter.usuarios.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.usuarios.dto.*;
import com.fortytwo.demeter.usuarios.model.User;
import com.fortytwo.demeter.usuarios.model.UserRole;
import com.fortytwo.demeter.usuarios.repository.UserRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class UserService {
    @Inject UserRepository userRepository;

    public PagedResponse<UserDTO> findAll(int page, int size) {
        var query = userRepository.findAll();
        var list = query.page(Page.of(page, size)).list();
        return PagedResponse.of(list.stream().map(UserDTO::from).toList(), page, size, query.count());
    }

    public UserDTO findById(UUID id) {
        return UserDTO.from(userRepository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("User", id)));
    }

    public UserDTO findByExternalId(String externalId) {
        return UserDTO.from(userRepository.findByExternalId(externalId).orElseThrow(() -> new EntityNotFoundException("User", externalId)));
    }

    @Transactional
    public UserDTO findOrCreateFromExternalId(String externalId, String email, String name) {
        return UserDTO.from(userRepository.findByExternalId(externalId).orElseGet(() -> {
            User u = new User();
            u.setExternalId(externalId);
            u.setEmail(email);
            u.setName(name);
            u.setRole(UserRole.VIEWER);
            userRepository.persist(u);
            return u;
        }));
    }

    @Transactional
    public UserDTO create(CreateUserRequest req) {
        User u = new User();
        u.setExternalId(UUID.randomUUID().toString());
        u.setEmail(req.email());
        u.setName(req.name());
        u.setRole(req.role() != null ? UserRole.valueOf(req.role()) : UserRole.VIEWER);
        userRepository.persist(u);
        return UserDTO.from(u);
    }

    @Transactional
    public UserDTO update(UUID id, UpdateUserRequest req) {
        User u = userRepository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (req.name() != null) u.setName(req.name());
        if (req.role() != null) u.setRole(UserRole.valueOf(req.role()));
        if (req.active() != null) u.setActive(req.active());
        return UserDTO.from(u);
    }

    @Transactional
    public void delete(UUID id) {
        User u = userRepository.findByIdOptional(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        u.setActive(false);
    }
}
