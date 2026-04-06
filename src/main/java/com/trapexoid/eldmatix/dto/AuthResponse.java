package com.trapexoid.eldmatix.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private UserInfo user;

    public AuthResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static class UserInfo {
        private String email;
        private String tenantId;
        private List<String> roles;

        public UserInfo(String email, String tenantId) {
            this.email = email;
            this.tenantId = tenantId;
        }

        public UserInfo(String email, String tenantId, List<String> roles) {
            this.email = email;
            this.tenantId = tenantId;
            this.roles = roles;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
