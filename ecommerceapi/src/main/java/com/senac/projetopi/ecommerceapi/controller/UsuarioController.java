package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.UsuarioDTO;
import com.senac.projetopi.ecommerceapi.model.Usuario;
import com.senac.projetopi.ecommerceapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários ou filtra por nome (com paginação).
     * Acesso restrito a administradores.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Usuario>> listarUsuarios(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<Usuario> usuarios;

        if (nome != null && !nome.isEmpty()) {
            usuarios = usuarioService.buscarPorNome(nome, pageable);
        } else {
            usuarios = usuarioService.buscarTodos(pageable);
        }

        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca um usuário pelo ID.
     * Acesso restrito a administradores.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

   
    @PostMapping
    //Apenas administradores podem executar esta ação.
    @PreAuthorize("hasRole('ADMIN')")
    //Recebe os dados no formato JSON (via UsuarioDTO).
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        //Converte para um objeto Usuario.
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(usuarioDTO.getSenha());
        usuario.setGrupo(usuarioDTO.getGrupo());

        //Chama o service para: validar email e cpf. esconde senha e salvar
        Usuario novoUsuario = usuarioService.criar(usuario);
        //
        // Retorna resposta com status 201 (Created).
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * Apenas administradores podem executar esta ação.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setSenha(usuarioDTO.getSenha());
        usuario.setGrupo(usuarioDTO.getGrupo());

        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * Ativa ou desativa o status do usuário (ativo/inativo).
     * Apenas administradores podem executar esta ação.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> alternarStatusUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.alternarStatus(id);
        return ResponseEntity.ok(usuario);
    }
}
