package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.LoginDTO;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente/auth")
public class ClienteAuthController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            Optional<Cliente> clienteOpt = clienteService.buscarPorEmail(loginDTO.getEmail());

            if (clienteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Email ou senha inválidos");
            }

            Cliente cliente = clienteOpt.get();

            if (!cliente.isAtivo()) {
                return ResponseEntity.badRequest().body("Sua conta está inativa");
            }

            if (!passwordEncoder.matches(loginDTO.getSenha(), cliente.getSenha())) {
                return ResponseEntity.badRequest().body("Email ou senha inválidos");
            }

            // Criar sessão para o cliente
            HttpSession session = request.getSession(true);
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteEmail", cliente.getEmail());
            session.setAttribute("clienteNome", cliente.getNome());

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Login realizado com sucesso");
            response.put("nome", cliente.getNome());
            response.put("email", cliente.getEmail());

            return ResponseEntity.ok(response);
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

    @GetMapping("/status")
    public ResponseEntity<?> verificarAutenticacao(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean autenticado = session != null && session.getAttribute("clienteId") != null;

        Map<String, Object> response = new HashMap<>();
        response.put("autenticado", autenticado);

        if (autenticado) {
            response.put("clienteId", session.getAttribute("clienteId"));
            response.put("clienteEmail", session.getAttribute("clienteEmail"));
            response.put("clienteNome", session.getAttribute("clienteNome"));
        }

        return ResponseEntity.ok(response);
    }
}