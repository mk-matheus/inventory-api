package com.mkmateus.inventory_api.tenant;

import com.mkmateus.inventory_api.entity.Tenant;
import com.mkmateus.inventory_api.repository.TenantRepository;
import com.mkmateus.inventory_api.tenant.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Order(1)
@RequiredArgsConstructor
public class TenantFilter implements Filter {

    private final TenantRepository tenantRepository;

    private static final String TENANT_HEADER = "X-Tenant-ID";

    // endpoints que não precisam de tenant (gestão da plataforma)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/tenants",
            "/actuator"
    );

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // deixa passar sem validação de tenant
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String slug = httpRequest.getHeader(TENANT_HEADER);

        if (slug == null || slug.isBlank()) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("Header X-Tenant-ID is required");
            return;
        }

        // valida se o tenant existe e está ativo
        Optional<Tenant> tenant = tenantRepository.findBySlugAndActiveTrue(slug);

        if (tenant.isEmpty()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Invalid or inactive tenant: " + slug);
            return;
        }

        try {
            // seta o schemaName (não o slug) — é o que o banco usa
            TenantContext.setTenant(tenant.get().getSchemaName());
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}