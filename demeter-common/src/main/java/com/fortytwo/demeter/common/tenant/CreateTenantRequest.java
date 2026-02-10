package com.fortytwo.demeter.common.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record CreateTenantRequest(
    @NotBlank @Size(min = 2, max = 64) String id,
    @NotBlank String name,
    @NotBlank String industry,
    Map<String, Object> theme,
    List<String> enabledModules,
    Map<String, Object> settings
) {}
