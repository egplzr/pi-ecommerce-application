package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.model.Carrinho;
import com.senac.projetopi.ecommerceapi.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @GetMapping
    public ResponseEntity<Carrinho> getCarrinho(HttpSession session) {
        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.getCarrinho(sessionId);
        return ResponseEntity.ok(carrinho);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Carrinho> adicionarItem(
            @RequestParam Long produtoId,
            @RequestParam(defaultValue = "1") int quantidade,
            HttpSession session) {

        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.adicionarItem(sessionId, produtoId, quantidade);
        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping("/remover/{produtoId}")
    public ResponseEntity<Carrinho> removerItem(
            @PathVariable Long produtoId,
            HttpSession session) {

        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.removerItem(sessionId, produtoId);
        return ResponseEntity.ok(carrinho);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Carrinho> atualizarQuantidade(
            @RequestParam Long produtoId,
            @RequestParam int quantidade,
            HttpSession session) {

        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.atualizarQuantidade(sessionId, produtoId, quantidade);
        return ResponseEntity.ok(carrinho);
    }

    @PostMapping("/calcular-frete")
    public ResponseEntity<Carrinho> calcularFrete(
            @RequestParam String cep,
            HttpSession session) {

        String sessionId = session.getId();
        Carrinho carrinho = carrinhoService.calcularFrete(sessionId, cep);
        return ResponseEntity.ok(carrinho);
    }

    @DeleteMapping("/limpar")
    public ResponseEntity<Map<String, String>> limparCarrinho(HttpSession session) {
        String sessionId = session.getId();
        carrinhoService.limparCarrinho(sessionId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Carrinho limpo com sucesso");
        return ResponseEntity.ok(response);
    }
}