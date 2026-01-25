package com.eldmatix.CasbinDemo.config;

import com.eldmatix.CasbinDemo.security.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolver() {
            @Override
            public String resolveCurrentTenantIdentifier() {
                String tenant = TenantContext.currentTenant();
                return (tenant != null) ? tenant : "DEFAULT";
            }

            @Override
            public boolean validateExistingCurrentSessions() {
                return false;
            }
        };
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CurrentTenantIdentifierResolver resolver) {
        // In Hibernate 6/7, we register the resolver
        return hibernateProperties -> hibernateProperties.put("hibernate.tenant_identifier_resolver", resolver);
    }
}
