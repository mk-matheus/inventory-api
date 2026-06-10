package com.mkmateus.inventory_api.config;

import com.mkmateus.inventory_api.tenant.TenantSchemaResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

    private final TenantSchemaResolver tenantSchemaResolver;

    public HibernateConfig(TenantSchemaResolver tenantSchemaResolver) {
        this.tenantSchemaResolver = tenantSchemaResolver;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.rafael.inventoryapi.entity");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(hibernateProperties());

        return factory;
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.tenant_identifier_resolver",
                tenantSchemaResolver);
        props.put("hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect");

        return props;
    }
}