package com.eldmatix.CasbinDemo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.ScopedValue;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String tenantId = "DEFAULT";

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (tokenProvider.validateToken(token)) {
                username = tokenProvider.extractUsername(token);
                // Assume tenantId might be null if not present, use default
                String extractedTenant = tokenProvider.extractTenantId(token);
                if (extractedTenant != null) {
                    tenantId = extractedTenant;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        final String finalTenantId = tenantId;

        // Execute downstream chain within the scope
        try {
            ScopedValue.where(TenantContext.TENANT_ID, finalTenantId).run(() -> {
                try {
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ServletException) {
                throw (ServletException) cause;
            } else if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw e;
            }
        }
    }
}
