package com.trapexoid.eldmatix.security;

import java.lang.ScopedValue;
import java.util.Optional;

/**
 * Modern TenantContext using Java ScopedValue.
 */
public class TenantContext {
    public static final ScopedValue<String> TENANT_ID = ScopedValue.newInstance();

    public static Optional<String> optionalCurrentTenant() {
        if (!TENANT_ID.isBound()) {
            return Optional.empty();
        }

        String tenantId = TENANT_ID.get();
        if (tenantId == null || tenantId.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(tenantId);
    }

    public static String currentTenant() {
        return optionalCurrentTenant()
                .orElseThrow(() -> new IllegalStateException("No tenant bound to the current request"));
    }
}
