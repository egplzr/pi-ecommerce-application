package com.senac.projetopi.ecommerceapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loja/perfil")
public class ClientePerfilController {

    @GetMapping
    public String perfilCliente() {
        return "loja/perfil-cliente";
    }
}