package com.senac.projetopi.ecommerceapi.repository;

import com.senac.projetopi.ecommerceapi.model.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long> {

    List<ImagemProduto> findByProdutoId(Long produtoId);

    Optional<ImagemProduto> findByProdutoIdAndPrincipal(Long produtoId, boolean principal);

    void deleteByProdutoId(Long produtoId);

    long countByProdutoId(Long produtoId);
}