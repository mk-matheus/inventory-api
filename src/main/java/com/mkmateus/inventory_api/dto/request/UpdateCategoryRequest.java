package com.mkmateus.inventory_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank String name,
        String description
) {}