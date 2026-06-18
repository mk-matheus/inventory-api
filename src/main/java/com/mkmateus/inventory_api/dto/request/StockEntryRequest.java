package com.mkmateus.inventory_api.dto.request;

import com.mkmateus.inventory_api.entity.StockEntry;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockEntryRequest(
        @NotNull StockEntry.Type type,
        @NotNull @Positive Integer quantity,
        String reason
) {}