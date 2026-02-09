package com.fortytwo.demeter.common.tenant;

import java.util.Set;

public record TenantConfig(
    String id,
    String name,
    String industry,
    Set<String> enabledModules
) {}
