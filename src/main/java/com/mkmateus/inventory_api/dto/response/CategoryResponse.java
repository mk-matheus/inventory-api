package com.mkmateus.inventory_api.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description
) {}