package com.senac.projetopi.ecommerceapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

        // Obter sessão sem criar uma nova
        HttpSession session = request.getSession(false);

        // Verificar se já existe uma sessão de cliente válida
        if (session != null && session.getAttribute("clienteId") != null) {
            return "redirect:/loja";
        }

        if (error != null) {
            model.addAttribute("erro", error);
        }

        if (logout != null) {
            model.addAttribute("mensagem", "Você saiu do sistema com sucesso");
        }

        model.addAttribute("title", "Login do Cliente");
        model.addAttribute("content", "loja/login-cliente :: content");

        return "loja/base";
    }
}