package com.trapexoid.eldmatix.security;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CasbinGuard implements AuthorizationGuard {

    private final Enforcer enforcer;

    public CasbinGuard(Enforcer enforcer) {
        this.enforcer = enforcer;
    }

    @Override
    public boolean allow(String object, String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String dom = TenantContext.optionalCurrentTenant().orElse(null);
        if (dom == null) {
            return false;
        }

        return enforcer.enforce(auth.getName(), dom, object, action);
    }
}
