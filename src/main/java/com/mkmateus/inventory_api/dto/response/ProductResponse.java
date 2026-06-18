package com.mkmateus.inventory_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        CategoryResponse category,  // null se não tiver categoria
        boolean active,
        LocalDateTime createdAt
) {}