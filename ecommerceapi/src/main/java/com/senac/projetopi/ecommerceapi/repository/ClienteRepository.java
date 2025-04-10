package com.senac.projetopi.ecommerceapi.repository;


import com.senac.projetopi.ecommerceapi.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}

