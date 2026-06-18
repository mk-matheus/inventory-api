package com.mkmateus.inventory_api.exception;

public class TenantNotFoundException extends BusinessException {
    public TenantNotFoundException(String slug) {
        super("Tenant não encontrado: " + slug);
    }
}