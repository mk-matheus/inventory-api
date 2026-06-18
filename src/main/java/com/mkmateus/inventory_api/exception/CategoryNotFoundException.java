package com.mkmateus.inventory_api.exception;

import java.util.UUID;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(UUID id) {
        super("Categoria não encontrada: " + id);
    }
}