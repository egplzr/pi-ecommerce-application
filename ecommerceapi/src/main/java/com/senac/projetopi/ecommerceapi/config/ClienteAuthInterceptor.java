package com.senac.projetopi.ecommerceapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ClienteAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Obter a sessão sem criar uma nova
        HttpSession session = request.getSession(false);

        // Verificar se o cliente está logado
        if (session == null || session.getAttribute("clienteId") == null) {
            // Redirecionar para a página de login com mensagem de erro
            response.sendRedirect("/loja/login-cliente?error=Você precisa estar logado para acessar esta página");
            return false;
        }

        return true;
    }
}