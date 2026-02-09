package com.fortytwo.demeter.common.tenant;

import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class DemeterTenantResolver implements TenantResolver {

    private static final String DEFAULT_TENANT = "default";
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_CLAIM = "tenant_id";

    @Inject
    JsonWebToken jwt;

    @Inject
    RoutingContext routingContext;

    @Inject
    TenantContext tenantContext;

    @Override
    public String getDefaultTenantId() {
        return DEFAULT_TENANT;
    }

    @Override
    public String resolveTenantId() {
        String tenantId = null;

        // Priority 1: JWT claim
        if (jwt != null && jwt.containsClaim(TENANT_CLAIM)) {
            tenantId = jwt.getClaim(TENANT_CLAIM);
        }

        // Priority 2: Header fallback (useful for dev/testing)
        if (tenantId == null && routingContext != null) {
            tenantId = routingContext.request().getHeader(TENANT_HEADER);
        }

        // Priority 3: Default
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT;
        }

        // Store in request-scoped context for RLS
        tenantContext.setCurrentTenantId(tenantId);

        return tenantId;
    }
}
