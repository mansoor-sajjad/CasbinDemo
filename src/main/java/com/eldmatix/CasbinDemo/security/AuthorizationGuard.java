package com.eldmatix.CasbinDemo.security;

@FunctionalInterface
public interface AuthorizationGuard {
    boolean allow(String object, String action);
}
