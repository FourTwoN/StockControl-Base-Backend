package com.fortytwo.demeter.common.tenant;

import io.agroal.api.AgroalPoolInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class RlsConnectionCustomizer implements AgroalPoolInterceptor {

    @Inject
    TenantContext tenantContext;

    @Override
    public void onConnectionAcquire(Connection connection) {
        String tenantId = tenantContext.getCurrentTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            try (var stmt = connection.prepareStatement("SET app.current_tenant = ?")) {
                stmt.setString(1, tenantId);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set tenant context on connection", e);
            }
        }
    }

    @Override
    public void onConnectionReturn(Connection connection) {
        try (var stmt = connection.prepareStatement("RESET app.current_tenant")) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset tenant context on connection", e);
        }
    }
}
