package com.senac.projetopi.ecommerceapi.dto;

/**
 * Cada item que veio do front-end no checkout.
 * O front-end envia, para cada item:
 *   - produtoId: identificador do produto (Long),
 *   - quantidade: nº de unidades (Integer),
 *   - precoUnitario: preço unitário como Double ou BigDecimal,
 *   - nome: (opcional, p/ exibir no front ou recálculo, mas o critério absoluto é usar o preço do banco).
 */
public class ItemDTO {
    private Long produtoId;
    private Integer quantidade;
    private Double precoUnitario;
    private String nome;

    // Getters e Setters

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

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
