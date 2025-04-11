package com.senac.projetopi.ecommerceapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ClienteAuthConfig implements WebMvcConfigurer {

    @Autowired
    private ClienteAuthInterceptor clienteAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Aplicar o interceptor apenas às rotas que requerem autenticação do cliente
        registry.addInterceptor(clienteAuthInterceptor)
                .addPathPatterns("/loja/minha-conta/**", "/loja/checkout/**", "/loja/pedidos/**")
                .excludePathPatterns("/loja", "/loja/produto/**", "/loja/login-cliente", "/loja/cadastro-cliente", "/api/cliente-auth/**");
    }
}