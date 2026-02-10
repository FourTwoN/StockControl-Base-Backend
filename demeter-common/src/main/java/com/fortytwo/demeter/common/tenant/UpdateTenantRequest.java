package com.fortytwo.demeter.common.tenant;

import java.util.List;
import java.util.Map;

public record UpdateTenantRequest(
    String name,
    String industry,
    Map<String, Object> theme,
    List<String> enabledModules,
    Map<String, Object> settings,
    Boolean active
) {}
