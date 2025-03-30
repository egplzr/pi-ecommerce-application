package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.exception.RecursoNaoEncontradoException;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.Usuario;
import com.senac.projetopi.ecommerceapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidadorCpfService validadorCpfService;

    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Page<Usuario> buscarPorNome(String nome, Pageable pageable) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario criar(Usuario usuario) {
        validarUsuario(usuario);

        if (!validadorCpfService.validar(usuario.getCpf())) {
            throw new ValidacaoException("CPF inválido");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id);

        boolean isProprioUsuario = isUsuarioAtual(usuarioExistente);

        if (!usuarioExistente.getCpf().equals(usuarioAtualizado.getCpf())) {
            if (!validadorCpfService.validar(usuarioAtualizado.getCpf())) {
                throw new ValidacaoException("CPF inválido");
            }
        }

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());

        if (!isProprioUsuario && usuarioAtualizado.getGrupo() != null) {
            usuarioExistente.setGrupo(usuarioAtualizado.getGrupo());
        }

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public Usuario alternarStatus(Long id) {
        Usuario usuario = buscarPorId(id);

        if (isUsuarioAtual(usuario)) {
            throw new ValidacaoException("Não é permitido alterar o status do próprio usuário logado");
        }

        usuario.setAtivo(!usuario.isAtivo());

        return usuarioRepository.save(usuario);
    }

    private boolean isUsuarioAtual(Usuario usuario) {
        String emailUsuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuario.getEmail().equals(emailUsuarioLogado);
    }

    private void validarUsuario(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ValidacaoException("Email já cadastrado");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new ValidacaoException("CPF já cadastrado");
        }
    }
}