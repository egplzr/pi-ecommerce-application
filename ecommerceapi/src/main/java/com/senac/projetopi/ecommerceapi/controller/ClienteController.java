package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.ClienteDTO;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Buscar cliente por ID (acesso para ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    // Criar um novo cliente (Acessível para qualquer pessoa)
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Atualizar os dados de um cliente (acesso para ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO) {

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setSenha(clienteDTO.getSenha());
        cliente.setEnderecoFaturamento(clienteDTO.getEnderecoFaturamento());
        cliente.setEnderecosEntrega(clienteDTO.getEnderecosEntrega());

        Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
        return ResponseEntity.ok(clienteAtualizado);
    }

    // Alternar o status (ativo/inativo) de um cliente (acesso para ADMIN)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Cliente> alternarStatusCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.alternarStatus(id);
        return ResponseEntity.ok(cliente);
    }
}