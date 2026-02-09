package com.fortytwo.demeter.common.auth;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.Set;

@RequestScoped
public class CurrentUser {

    @Inject
    JsonWebToken jwt;

    public String getUserId() {
        return jwt.getSubject();
    }

    public String getEmail() {
        return jwt.getClaim("email");
    }

    public String getTenantId() {
        return jwt.getClaim("tenant_id");
    }

    public Set<String> getRoles() {
        return jwt.getGroups();
    }

    public String getName() {
        return jwt.getClaim("name");
    }
}
