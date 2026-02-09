package com.fortytwo.demeter.common.tenant;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TenantContext {

    private String currentTenantId;

    public String getCurrentTenantId() {
        return currentTenantId;
    }

    public void setCurrentTenantId(String tenantId) {
        this.currentTenantId = tenantId;
    }
}
