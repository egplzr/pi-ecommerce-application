package com.senac.projetopi.ecommerceapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO que representa um único item do carrinho,
 * conforme enviado pelo front-end no JSON de finalização.
 */
public class ItemCarrinhoDTO {

    @NotNull(message = "produtoId não pode ser nulo")
    private Long produtoId;

    @NotNull(message = "quantidade não pode ser nula")
    @Min(value = 1, message = "quantidade mínima = 1")
    private Integer quantidade;

    @NotNull(message = "preço unitário não pode ser nulo")
    private BigDecimal precoUnitario;

    @NotNull(message = "nome do produto não pode ser nulo")
    private String nome;

    public ItemCarrinhoDTO() { }

    public ItemCarrinhoDTO(Long produtoId, Integer quantidade, BigDecimal precoUnitario, String nome) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.nome = nome;
    }

    public Long getProdutoId() {
        return produtoId;
    }
    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
}
