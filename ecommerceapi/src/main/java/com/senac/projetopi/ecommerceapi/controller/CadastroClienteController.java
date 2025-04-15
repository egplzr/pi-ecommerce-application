package com.senac.projetopi.ecommerceapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class CadastroClienteController {
        @GetMapping("/loja/cadastro-cliente")
        public String cadastro() {
            return "loja/cadastro-cliente"; // Retorna o template Thymeleaf dentro de templates/loja/
        }
    }
