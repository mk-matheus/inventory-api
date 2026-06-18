package com.mkmateus.inventory_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Applying public schema migrations...");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("public")
                .locations("classpath:db/migration/public")
                .load();

        flyway.migrate();

        log.info("Public schema migrations applied.");
    }
}