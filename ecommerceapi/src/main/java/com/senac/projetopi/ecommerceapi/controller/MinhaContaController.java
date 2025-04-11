package com.senac.projetopi.ecommerceapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/loja/minha-conta")
public class MinhaContaController {

    @GetMapping
    public String minhaContaDashboard(Model model, HttpServletRequest request) {
        // A sessão já será verificada pelo interceptor, mas podemos usar os dados aqui
        HttpSession session = request.getSession(false);
        Long clienteId = (Long) session.getAttribute("clienteId");
        String nome = (String) session.getAttribute("clienteNome");

        model.addAttribute("clienteId", clienteId);
        model.addAttribute("nome", nome);

        return "loja/minha-conta";
    }

    @GetMapping("/dados")
    public String meusDados() {
        // Esta página seria implementada para editar os dados do cliente
        return "loja/minha-conta-dados";
    }

    @GetMapping("/enderecos")
    public String meusEnderecos() {
        // Esta página seria implementada para gerenciar endereços
        return "loja/minha-conta-enderecos";
    }
}
