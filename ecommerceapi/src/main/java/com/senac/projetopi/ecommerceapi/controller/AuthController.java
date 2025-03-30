package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.LoginDTO;
import com.senac.projetopi.ecommerceapi.dto.UsuarioDTO;
import com.senac.projetopi.ecommerceapi.model.Usuario;
import com.senac.projetopi.ecommerceapi.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha());

            Authentication auth = authenticationManager.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Login realizado com sucesso");
            response.put("email", auth.getName());
            response.put("roles", auth.getAuthorities());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no login: " + e.getMessage());
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            if (!usuarioDTO.getSenha().equals(usuarioDTO.getConfirmarSenha())) {
                return ResponseEntity.badRequest().body("As senhas não conferem");
            }

            Usuario usuario = new Usuario();
            usuario.setNome(usuarioDTO.getNome());
            usuario.setCpf(usuarioDTO.getCpf());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setSenha(usuarioDTO.getSenha());
            usuario.setGrupo(usuarioDTO.getGrupo());

            Usuario novoUsuario = usuarioService.criar(usuario);

            // Remove a senha do objeto de retorno por segurança
            novoUsuario.setSenha(null);

            return ResponseEntity.ok(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro no cadastro: " + e.getMessage());
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", auth.getName());
            userInfo.put("roles", auth.getAuthorities());
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.badRequest().body("Usuário não autenticado");
    }
}
