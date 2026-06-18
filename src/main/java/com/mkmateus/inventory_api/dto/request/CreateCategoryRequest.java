package com.mkmateus.inventory_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank String name,
        String description
) {}