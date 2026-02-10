package com.fortytwo.demeter.common.tenant;

import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TenantConfigService {

    @Inject
    TenantRepository tenantRepository;

    public TenantConfigResponse findConfig(String tenantId) {
        Tenant tenant = tenantRepository.findByIdOptional(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));
        return TenantConfigResponse.from(tenant);
    }

    public List<Tenant> findAll() {
        return tenantRepository.listAll();
    }

    @Transactional
    public Tenant create(CreateTenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setId(request.id());
        tenant.setName(request.name());
        tenant.setIndustry(request.industry());
        tenant.setTheme(request.theme() != null ? request.theme() : Map.of());
        tenant.setEnabledModules(request.enabledModules() != null ? request.enabledModules() : List.of());
        tenant.setSettings(request.settings() != null ? request.settings() : Map.of());
        tenantRepository.persist(tenant);
        return tenant;
    }

    @Transactional
    public Tenant update(String tenantId, UpdateTenantRequest request) {
        Tenant tenant = tenantRepository.findByIdOptional(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));

        if (request.name() != null) tenant.setName(request.name());
        if (request.industry() != null) tenant.setIndustry(request.industry());
        if (request.theme() != null) tenant.setTheme(request.theme());
        if (request.enabledModules() != null) tenant.setEnabledModules(request.enabledModules());
        if (request.settings() != null) tenant.setSettings(request.settings());
        if (request.active() != null) tenant.setActive(request.active());

        return tenant;
    }

    @Transactional
    public void delete(String tenantId) {
        Tenant tenant = tenantRepository.findByIdOptional(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant", tenantId));
        tenantRepository.delete(tenant);
    }
}
