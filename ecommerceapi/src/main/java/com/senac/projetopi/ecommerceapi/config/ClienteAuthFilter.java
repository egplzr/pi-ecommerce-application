package com.senac.projetopi.ecommerceapi.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClienteAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Verificar se a requisição é para um endpoint protegido do cliente
        if (isClienteProtectedPath(path)) {
            HttpSession session = httpRequest.getSession(false);

            // Verificar se o cliente está autenticado
            if (session == null || session.getAttribute("clienteId") == null) {
                // Cliente não está autenticado, redirecionar para a página de login
                HttpServletResponse httpResponse = (HttpServletResponse) response;

                // Se for uma requisição AJAX, retornar status 401
                if ("XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"))) {
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Redirecionar para a página de login
                httpResponse.sendRedirect("/loja/login-cliente");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isClienteProtectedPath(String path) {
        return path.startsWith("/api/cliente/") && !path.startsWith("/api/cliente/auth/")
                || path.startsWith("/loja/perfil");
    }
}