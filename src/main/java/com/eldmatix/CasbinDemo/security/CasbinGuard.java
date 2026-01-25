package com.eldmatix.CasbinDemo.security;

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
        String sub = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        String dom = TenantContext.currentTenant();

        return enforcer.enforce(sub, dom, object, action);
    }
}
