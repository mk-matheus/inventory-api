package com.mkmateus.inventory_api.tenant;

import com.mkmateus.inventory_api.tenant.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1) // esse filter roda antes de qualquer outro
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String tenant = httpRequest.getHeader(TENANT_HEADER);

        // se não vier o header, rejeita a requisição
        if (tenant == null || tenant.isBlank()) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("Header X-Tenant-ID is required");
            return;
        }

        try {
            TenantContext.setTenant(tenant);
            chain.doFilter(request, response); // segue pro próximo filter/controller
        } finally {
            TenantContext.clear(); // executa SEMPRE, mesmo se der erro
        }
    }
}
