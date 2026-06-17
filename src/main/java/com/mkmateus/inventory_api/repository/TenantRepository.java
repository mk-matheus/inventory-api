package com.mkmateus.inventory_api.repository;

import com.mkmateus.inventory_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    // usado pelo TenantFilter pra validar se o tenant existe
    Optional<Tenant> findBySlugAndActiveTrue(String slug);

    // usado na criação de tenant pra evitar duplicatas
    boolean existsBySlug(String slug);

    // usado na criação de tenant pra evitar duplicatas no schema
    boolean existsBySchemaName(String schemaName);
}