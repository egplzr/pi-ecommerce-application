package com.senac.projetopi.ecommerceapi.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginClienteController {

    @GetMapping("/loja/login-cliente")
    public String login() {
        return "loja/login-cliente"; // Retorna o template Thymeleaf dentro de templates/loja/
    }
}
