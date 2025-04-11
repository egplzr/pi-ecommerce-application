package com.senac.projetopi.ecommerceapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/loja/pedidos")
public class PedidosClienteController {

    @GetMapping
    public String meusPedidos(Model model, HttpServletRequest request) {
        // A sessão já será verificada pelo interceptor, mas podemos usar os dados aqui
        HttpSession session = request.getSession(false);
        Long clienteId = (Long) session.getAttribute("clienteId");
        String nome = (String) session.getAttribute("clienteNome");

        model.addAttribute("clienteId", clienteId);
        model.addAttribute("nome", nome);

        // Aqui seriam carregados os pedidos do cliente do banco de dados

        return "loja/pedidos";
    }
}