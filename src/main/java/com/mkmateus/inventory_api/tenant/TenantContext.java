package com.mkmateus.inventory_api.tenant;

public class TenantContext {

    // ThreadLocal garante isolamento por requisição
    private static final ThreadLocal<String> CURRENT_TENANT =
            new ThreadLocal<>();

    // Impede instanciação — essa classe só tem métodos estáticos
    private TenantContext() {}

    public static void setTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getTenant() {
        return CURRENT_TENANT.get();
    }

    // MUITO importante: limpa a thread após a requisição terminar
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
