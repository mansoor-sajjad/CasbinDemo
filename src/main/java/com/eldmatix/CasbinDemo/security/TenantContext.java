package com.eldmatix.CasbinDemo.security;

import java.lang.ScopedValue;

/**
 * Modern TenantContext using Java ScopedValue.
 */
public class TenantContext {
    public static final ScopedValue<String> TENANT_ID = ScopedValue.newInstance();

    public static String currentTenant() {
        return TENANT_ID.isBound() ? TENANT_ID.get() : "DEFAULT";
    }
}
