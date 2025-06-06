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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public Cliente atualizarPerfilCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = buscarPorId(id);

        if (clienteDTO.getNome() != null && !clienteDTO.getNome().isEmpty()) {
            cliente.setNome(clienteDTO.getNome());
        }

        if (clienteDTO.getGenero() != null && !clienteDTO.getGenero().isEmpty()) {
            try {
                cliente.setGenero(Cliente.Genero.valueOf(clienteDTO.getGenero()));
            } catch (IllegalArgumentException e) {
                throw new ValidacaoException("Gênero inválido. Use MASCULINO, FEMININO ou OUTRO");
            }
        }

        if (clienteDTO.getDataNascimento() != null) {
            try {
                LocalDate date = LocalDate.parse(clienteDTO.getDataNascimento().toString());
                cliente.setDataNascimento(LocalDateTime.of(date, LocalTime.MIDNIGHT));
            } catch (Exception e) {
                throw new ValidacaoException("Formato de data inválido. Use o formato yyyy-MM-dd");
            }
        }

        // Só atualiza a senha se não for a string especial "manterSenhaAtual"
        if (clienteDTO.getSenha() != null && !clienteDTO.getSenha().isEmpty()
                && !clienteDTO.getSenha().equals("manterSenhaAtual")) {
            cliente.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente adicionarEnderecoEntrega(Long id, String novoEndereco) {
        if (novoEndereco == null || novoEndereco.trim().isEmpty()) {
            throw new ValidacaoException("Endereço não pode ser vazio");
        }

        Cliente cliente = buscarPorId(id);

        if (cliente.getEnderecosEntrega() == null) {
            cliente.setEnderecosEntrega(new java.util.ArrayList<>());
        }

        cliente.getEnderecosEntrega().add(novoEndereco);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente removerEnderecoEntrega(Long id, int index) {
        Cliente cliente = buscarPorId(id);

        if (cliente.getEnderecosEntrega() == null || cliente.getEnderecosEntrega().isEmpty()) {
            throw new ValidacaoException("Não há endereços para remover");
        }

        if (index < 0 || index >= cliente.getEnderecosEntrega().size()) {
            throw new ValidacaoException("Índice de endereço inválido");
        }

        cliente.getEnderecosEntrega().remove(index);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente definirEnderecoPadrao(Long id, int index) {
        Cliente cliente = buscarPorId(id);

        if (cliente.getEnderecosEntrega() == null || cliente.getEnderecosEntrega().isEmpty()) {
            throw new ValidacaoException("Não há endereços cadastrados");
        }

        if (index < 0 || index >= cliente.getEnderecosEntrega().size()) {
            throw new ValidacaoException("Índice de endereço inválido");
        }

        String enderecoPadrao = cliente.getEnderecosEntrega().get(index);
        cliente.getEnderecosEntrega().remove(index);
        cliente.getEnderecosEntrega().add(0, enderecoPadrao);

        return clienteRepository.save(cliente);
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
