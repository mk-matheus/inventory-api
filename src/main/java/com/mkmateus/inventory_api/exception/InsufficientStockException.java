package com.mkmateus.inventory_api.exception;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format(
                "Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                productName, available, requested));
    }
}