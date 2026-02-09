package com.fortytwo.demeter.common.auth;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Security configuration placeholder.
 * OIDC is configured via application.properties (Auth0 provider).
 */
@ApplicationScoped
public class SecurityConfig {

    public static final String OIDC_PROVIDER = "auth0";
}
