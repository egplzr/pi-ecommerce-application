package com.senac.projetopi.ecommerceapi.repository;

import com.senac.projetopi.ecommerceapi.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
