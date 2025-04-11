package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.model.Cliente;
import com.senac.projetopi.ecommerceapi.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteAuthService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Cliente> autenticarCliente(String email, String senha) {
        Optional<Cliente> clienteOptional = clienteRepository.findByEmail(email);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            // Verifica se a senha fornecida corresponde à senha armazenada
            if (passwordEncoder.matches(senha, cliente.getSenha())) {
                // Cliente autenticado com sucesso
                return Optional.of(cliente);
            }
        }

        // Autenticação falhou
        return Optional.empty();
    }
}