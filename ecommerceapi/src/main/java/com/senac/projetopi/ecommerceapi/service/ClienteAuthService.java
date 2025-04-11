package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.LoginDTO;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.Cliente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteAuthService {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Autentica um cliente usando email e senha
     *
     * @param loginDTO DTO contendo email e senha
     * @param session A sessão HTTP atual
     * @return O cliente autenticado
     * @throws ValidacaoException se as credenciais forem inválidas
     */
    public Cliente autenticar(LoginDTO loginDTO, HttpSession session) {
        // Buscar cliente pelo email
        Optional<Cliente> clienteOpt = clienteService.buscarPorEmail(loginDTO.getEmail());

        if (clienteOpt.isEmpty()) {
            throw new ValidacaoException("Email ou senha inválidos");
        }

        Cliente cliente = clienteOpt.get();

        // Verificar se o cliente está ativo
        if (!cliente.isAtivo()) {
            throw new ValidacaoException("Sua conta está inativa");
        }

        // Verificar se a senha é válida
        if (!passwordEncoder.matches(loginDTO.getSenha(), cliente.getSenha())) {
            throw new ValidacaoException("Email ou senha inválidos");
        }

        // Armazenar dados do cliente na sessão
        session.setAttribute("clienteId", cliente.getId());
        session.setAttribute("clienteEmail", cliente.getEmail());
        session.setAttribute("clienteNome", cliente.getNome());

        return cliente;
    }

    /**
     * Verifica se há um cliente autenticado na sessão atual
     *
     * @param session A sessão HTTP atual
     * @return true se existir um cliente autenticado, false caso contrário
     */
    public boolean isClienteAutenticado(HttpSession session) {
        return session != null && session.getAttribute("clienteId") != null;
    }

    /**
     * Obtém o ID do cliente autenticado
     *
     * @param session A sessão HTTP atual
     * @return O ID do cliente autenticado, ou null se não houver cliente autenticado
     */
    public Long getClienteAutenticadoId(HttpSession session) {
        if (!isClienteAutenticado(session)) {
            return null;
        }

        return (Long) session.getAttribute("clienteId");
    }

    /**
     * Realiza o logout do cliente
     *
     * @param session A sessão HTTP atual
     */
    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}