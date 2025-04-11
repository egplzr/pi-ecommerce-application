package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.ClienteLoginDTO;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.service.ClienteAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente-auth")
public class ClienteAuthController {

    @Autowired
    private ClienteAuthService clienteAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ClienteLoginDTO loginDTO, HttpServletRequest request) {
        try {
            Optional<Cliente> clienteOptional = clienteAuthService.autenticarCliente(
                    loginDTO.getEmail(),
                    loginDTO.getSenha()
            );

            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();

                // Verificar se o cliente está ativo
                if (!cliente.isAtivo()) {
                    return ResponseEntity.badRequest().body("Cliente desativado. Entre em contato com o suporte.");
                }

                // Criar sessão para o cliente
                HttpSession session = request.getSession(true);
                session.setAttribute("clienteId", cliente.getId());
                session.setAttribute("clienteEmail", cliente.getEmail());
                session.setAttribute("clienteNome", cliente.getNome());

                Map<String, Object> response = new HashMap<>();
                response.put("mensagem", "Login realizado com sucesso");
                response.put("cliente", Map.of(
                        "id", cliente.getId(),
                        "nome", cliente.getNome(),
                        "email", cliente.getEmail()
                ));

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Email ou senha inválidos");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no login: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Logout realizado com sucesso");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("clienteId") != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("logado", true);
            response.put("cliente", Map.of(
                    "id", session.getAttribute("clienteId"),
                    "nome", session.getAttribute("clienteNome"),
                    "email", session.getAttribute("clienteEmail")
            ));

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(Map.of("logado", false));
        }
    }
}