package com.fortytwo.demeter.common.tenant;

import java.util.List;
import java.util.Map;

public record TenantConfigResponse(
    String id,
    String name,
    String industry,
    TenantTheme theme,
    List<String> enabledModules,
    Map<String, Object> settings
) {

    public static TenantConfigResponse from(Tenant tenant) {
        Map<String, Object> t = tenant.getTheme();
        var theme = new TenantTheme(
            str(t, "primary"),
            str(t, "secondary"),
            str(t, "accent"),
            str(t, "background"),
            str(t, "logoUrl"),
            str(t, "appName")
        );
        return new TenantConfigResponse(
            tenant.getId(),
            tenant.getName(),
            tenant.getIndustry(),
            theme,
            tenant.getEnabledModules(),
            tenant.getSettings()
        );
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}
