package com.senac.projetopi.ecommerceapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginPageController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model, HttpServletRequest request) {

        if (request.getUserPrincipal() != null) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("erro", "Nome de usuário ou senha inválidos");
        }

        if (logout != null) {
            model.addAttribute("mensagem", "Você saiu do sistema com sucesso");
        }

        if (expired != null) {
            model.addAttribute("erro", "Sua sessão expirou. Por favor, faça login novamente");
        }

        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
