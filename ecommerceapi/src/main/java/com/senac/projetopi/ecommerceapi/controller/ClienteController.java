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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public ResponseEntity<?> atualizarPerfil(@RequestBody ClienteDTO clienteDTO, HttpServletRequest request) {
        // Remova a anotação @Valid que força a validação dos campos obrigatórios

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

    @PostMapping
    public ResponseEntity<?> criarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNome(clienteDTO.getNome());
            cliente.setCpf(clienteDTO.getCpf());
            cliente.setEmail(clienteDTO.getEmail());
            cliente.setSenha(clienteDTO.getSenha());
            cliente.setEnderecoFaturamento(clienteDTO.getEnderecoFaturamento());
            cliente.setEnderecosEntrega(clienteDTO.getEnderecosEntrega());

            // Converter a data de nascimento de String para LocalDateTime
            if (clienteDTO.getDataNascimento() != null && !clienteDTO.getDataNascimento().toString().isEmpty()) {
                try {
                    // Se for uma string no formato yyyy-MM-dd (do input date HTML)
                    LocalDate date = LocalDate.parse(clienteDTO.getDataNascimento().toString());
                    cliente.setDataNascimento(LocalDateTime.of(date, LocalTime.MIDNIGHT));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Formato de data inválido. Use o formato yyyy-MM-dd");
                }
            }

            // Processar o gênero se fornecido
            if (clienteDTO.getGenero() != null && !clienteDTO.getGenero().isEmpty()) {
                try {
                    cliente.setGenero(Cliente.Genero.valueOf(clienteDTO.getGenero()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Gênero inválido. Use MASCULINO, FEMININO ou OUTRO");
                }
            }

            Cliente novoCliente = clienteService.criar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);  // Retorne sempre um JSON
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);  // Retorne sempre um JSON
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);  // Retorne sempre um JSON
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);  // Retorne sempre um JSON
        }
    }
}