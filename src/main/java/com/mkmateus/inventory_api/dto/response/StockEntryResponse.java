package com.mkmateus.inventory_api.dto.response;

import com.mkmateus.inventory_api.entity.StockEntry;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockEntryResponse(
        UUID id,
        UUID productId,
        String productName,
        StockEntry.Type type,
        Integer quantity,
        String reason,
        LocalDateTime createdAt
) {}