package com.senac.projetopi.ecommerceapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginClienteController {

    @GetMapping("/loja/login-cliente")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model,
            HttpServletRequest request) {

        // Verifica se já existe uma sessão de cliente
        if (request.getSession(false) != null && request.getSession().getAttribute("clienteId") != null) {
            return "redirect:/loja";
        }

        if (error != null) {
            model.addAttribute("erro", "Email ou senha inválidos");
        }

        if (logout != null) {
            model.addAttribute("mensagem", "Você saiu do sistema com sucesso");
        }

        return "loja/login-cliente";
    }
}