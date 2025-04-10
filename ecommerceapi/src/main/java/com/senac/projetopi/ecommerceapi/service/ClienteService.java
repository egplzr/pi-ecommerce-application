package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.ClienteDTO;
import com.senac.projetopi.ecommerceapi.exception.RecursoNaoEncontradoException;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidadorCpfService validadorCpfService;

    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        validarCliente(cliente);

        if (!validadorCpfService.validar(cliente.getCpf())) {
            throw new ValidacaoException("CPF inválido");
        }

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        boolean isProprioCliente = isClienteAtual(clienteExistente);

        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf())) {
            if (!validadorCpfService.validar(clienteAtualizado.getCpf())) {
                throw new ValidacaoException("CPF inválido");
            }
        }

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());

        if (!isProprioCliente && clienteAtualizado.getEnderecoFaturamento() != null) {
            clienteExistente.setEnderecoFaturamento(clienteAtualizado.getEnderecoFaturamento());
        }

        if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isEmpty()) {
            clienteExistente.setSenha(passwordEncoder.encode(clienteAtualizado.getSenha()));
        }

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public Cliente alternarStatus(Long id) {
        Cliente cliente = buscarPorId(id);

        if (isClienteAtual(cliente)) {
            throw new ValidacaoException("Não é permitido alterar o status do próprio cliente logado");
        }

        cliente.setAtivo(!cliente.isAtivo());

        return clienteRepository.save(cliente);
    }

    private boolean isClienteAtual(Cliente cliente) {
        // Aqui você pode obter o email do cliente logado (como feito no UsuarioService)
        String emailClienteLogado = "cliente@exemplo.com"; // Substitua pela lógica para obter o cliente logado
        return cliente.getEmail().equals(emailClienteLogado);
    }

    private void validarCliente(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ValidacaoException("Email já cadastrado");
        }

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ValidacaoException("CPF já cadastrado");
        }
    }
}

