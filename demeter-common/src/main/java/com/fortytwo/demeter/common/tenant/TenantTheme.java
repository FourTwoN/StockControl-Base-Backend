package com.fortytwo.demeter.common.tenant;

public record TenantTheme(
    String primary,
    String secondary,
    String accent,
    String background,
    String logoUrl,
    String appName
) {}
