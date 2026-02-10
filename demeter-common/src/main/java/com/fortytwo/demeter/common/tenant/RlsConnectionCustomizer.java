package com.fortytwo.demeter.common.tenant;

import io.agroal.api.AgroalPoolInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class RlsConnectionCustomizer implements AgroalPoolInterceptor {

    @Inject
    TenantContext tenantContext;

    @Override
    public void onConnectionAcquire(Connection connection) {
        String tenantId;
        try {
            tenantId = tenantContext.getCurrentTenantId();
        } catch (ContextNotActiveException ignored) {
            // Startup/background DB access (e.g. Flyway) can happen without request scope.
            return;
        }

        if (tenantId != null && !tenantId.isBlank()) {
            try (var stmt = connection.prepareStatement("SELECT set_config('app.current_tenant', ?, true)")) {
                stmt.setString(1, tenantId);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set tenant context on connection", e);
            }
        }
    }

    @Override
    public void onConnectionReturn(Connection connection) {
        try (var stmt = connection.prepareStatement("SELECT set_config('app.current_tenant', '', true)")) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset tenant context on connection", e);
        }
    }
}
