package com.trapexoid.eldmatix.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.trapexoid.eldmatix.security.TenantContext;

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
    public BeanPostProcessor hibernatePropertiesCustomizer(CurrentTenantIdentifierResolver resolver) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof LocalContainerEntityManagerFactoryBean factory) {
                    factory.getJpaPropertyMap().put("hibernate.tenant_identifier_resolver", resolver);
                }
                return bean;
            }
        };
    }
}
