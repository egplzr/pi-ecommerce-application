package com.senac.projetopi.ecommerceapi.repository;

import com.senac.projetopi.ecommerceapi.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE " +
            "(:nome IS NULL OR p.nome LIKE CONCAT('%', :nome, '%')) AND " +
            "(:codigo IS NULL OR p.codigo = :codigo) AND " +
            "(:ativo IS NULL OR p.ativo = :ativo)")
    Page<Produto> buscarComFiltros(
            @Param("nome") String nome,
            @Param("codigo") String codigo,
            @Param("ativo") Boolean ativo,
            Pageable pageable);
}