package com.trapexoid.eldmatix.config;

import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@Configuration
public class CasbinConfig {

    @Value("${casbin.model}")
    private Resource modelResource;

    @Bean
    public Enforcer enforcer(DataSource dataSource) throws Exception {
        // Initialize JDBC Adapter
        // It automatically detects database type from DataSource
        JDBCAdapter adapter = new JDBCAdapter(dataSource);

        // Load Model from classpath resource
        String modelText = StreamUtils.copyToString(modelResource.getInputStream(), StandardCharsets.UTF_8);
        Model model = new Model();
        model.loadModelFromText(modelText);

        return new Enforcer(model, adapter);
    }
}
