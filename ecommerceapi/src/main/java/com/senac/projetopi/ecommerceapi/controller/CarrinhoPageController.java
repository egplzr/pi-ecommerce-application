package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.model.Carrinho;
import com.senac.projetopi.ecommerceapi.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loja/carrinho")
public class CarrinhoPageController {

    @Autowired
    private CarrinhoService carrinhoService;

    @GetMapping
    public String paginaCarrinho(Model model, HttpSession session) {
        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.getCarrinho(sessionId);

        model.addAttribute("carrinho", carrinho);
        return "loja/carrinho";
    }
}