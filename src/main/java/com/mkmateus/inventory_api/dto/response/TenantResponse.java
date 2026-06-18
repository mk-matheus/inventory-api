package com.mkmateus.inventory_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String slug,
        String schemaName,
        boolean active,
        LocalDateTime createdAt
) {}