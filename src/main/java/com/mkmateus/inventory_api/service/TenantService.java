package com.mkmateus.inventory_api.service;

import com.mkmateus.inventory_api.dto.request.CreateTenantRequest;
import com.mkmateus.inventory_api.dto.response.TenantResponse;
import com.mkmateus.inventory_api.entity.Tenant;
import com.mkmateus.inventory_api.exception.BusinessException;
import com.mkmateus.inventory_api.exception.DuplicateResourceException;
import com.mkmateus.inventory_api.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        // deriva o schemaName do slug — substitui hífen por underscore
        // "loja-tio" → "loja_tio" (PostgreSQL não aceita hífen em schema names)
        String schemaName = request.slug().replace("-", "_");

        if (tenantRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException(
                    "Slug já utilizado: " + request.slug());
        }

        if (tenantRepository.existsBySchemaName(schemaName)) {
            throw new DuplicateResourceException(
                    "Schema já existe: " + schemaName);
        }

        // cria o schema no banco
        createSchema(schemaName);

        // roda as migrations no novo schema
        runMigrations(schemaName);

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .slug(request.slug())
                .schemaName(schemaName)
                .active(true)
                .build();

        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> findAll() {
        return tenantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void createSchema(String schemaName) {
        // validação crítica de segurança — previne SQL injection
        // schema name só pode ter letras minúsculas, números e underscore
        if (!schemaName.matches("^[a-z0-9_]+$")) {
            throw new BusinessException("Nome de schema inválido: " + schemaName);
        }

        log.info("Creating schema: {}", schemaName);

        // DDL não suporta prepared statements — por isso a validação acima é obrigatória
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        } catch (SQLException e) {
            throw new BusinessException("Erro ao criar schema: " + e.getMessage());
        }
    }

    private void runMigrations(String schemaName) {
        log.info("Running migrations on schema: {}", schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                // aponta para a pasta de migrations de tenants
                .locations("classpath:db/migration/tenants")
                .load();

        flyway.migrate();
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getSchemaName(),
                tenant.isActive(),
                tenant.getCreatedAt()
        );
    }
}