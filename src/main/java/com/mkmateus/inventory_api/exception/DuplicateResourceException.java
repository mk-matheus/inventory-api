package com.mkmateus.inventory_api.exception;

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}