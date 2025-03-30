package com.senac.projetopi.ecommerceapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/produtos")
public class ProdutoPageController {

    @GetMapping
    public String paginaProdutos(HttpServletRequest request) {
        // Verifica se o usuário está logado
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        // Verifica se o usuário tem permissão (ROLE_ADMIN ou ROLE_ESTOQUISTA)
        if (!request.isUserInRole("ADMIN") && !request.isUserInRole("ESTOQUISTA")) {
            return "redirect:/dashboard";
        }

        return "produtos";
    }
}