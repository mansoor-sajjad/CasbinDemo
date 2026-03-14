package com.trapexoid.eldmatix.security;

@FunctionalInterface
public interface AuthorizationGuard {
    boolean allow(String object, String action);
}
