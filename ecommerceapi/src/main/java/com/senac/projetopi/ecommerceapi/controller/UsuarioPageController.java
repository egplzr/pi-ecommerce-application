package com.senac.projetopi.ecommerceapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioPageController {

    @GetMapping
    public String paginaUsuarios(HttpServletRequest request) {
        // Verifica se o usuário está logado
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        // Verifica se o usuário tem permissão (ROLE_ADMIN)
        if (!request.isUserInRole("ADMIN")) {
            return "redirect:/dashboard";
        }

        return "usuarios";
    }
}