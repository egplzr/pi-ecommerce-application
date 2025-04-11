package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.ClienteDTO;
import com.senac.projetopi.ecommerceapi.dto.EnderecoDTO;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");
        Cliente cliente = clienteService.buscarPorId(clienteId);
        cliente.setSenha(null); // Não retornar a senha

        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPerfil(@Valid @RequestBody ClienteDTO clienteDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            Cliente clienteAtualizado = clienteService.atualizarPerfilCliente(clienteId, clienteDTO);
            clienteAtualizado.setSenha(null); // Não retornar a senha
            return ResponseEntity.ok(clienteAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/endereco")
    public ResponseEntity<?> adicionarEndereco(@Valid @RequestBody EnderecoDTO enderecoDTO, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            Cliente clienteAtualizado = clienteService.adicionarEnderecoEntrega(clienteId, enderecoDTO.getEndereco());

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Endereço adicionado com sucesso");
            response.put("enderecosEntrega", clienteAtualizado.getEnderecosEntrega());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/endereco/{index}")
    public ResponseEntity<?> removerEndereco(@PathVariable int index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            Cliente clienteAtualizado = clienteService.removerEnderecoEntrega(clienteId, index);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Endereço removido com sucesso");
            response.put("enderecosEntrega", clienteAtualizado.getEnderecosEntrega());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/endereco/{index}/padrao")
    public ResponseEntity<?> definirEnderecoPadrao(@PathVariable int index, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            Cliente clienteAtualizado = clienteService.definirEnderecoPadrao(clienteId, index);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Endereço padrão definido com sucesso");
            response.put("enderecosEntrega", clienteAtualizado.getEnderecosEntrega());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}