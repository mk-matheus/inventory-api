package com.mkmateus.inventory_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // habilita o @CreatedDate e @LastModifiedDate
public class AuditingConfig {
}