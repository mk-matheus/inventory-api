package com.mkmateus.inventory_api.exception;

import java.util.UUID;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(UUID id) {
        super("Produto não encontrado: " + id);
    }
}