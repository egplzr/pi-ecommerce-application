package com.senac.projetopi.ecommerceapi.model;

import java.math.BigDecimal;

public class CarrinhoItem {
    private Long produtoId;
    private String nome;
    private String codigo;
    private BigDecimal preco;
    private String imagemUrl;
    private int quantidade;
    private BigDecimal subtotal;

    public CarrinhoItem() {
    }

    public CarrinhoItem(Long produtoId, String nome, String codigo, BigDecimal preco, String imagemUrl, int quantidade) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.codigo = codigo;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
        this.quantidade = quantidade;
        this.calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = this.preco.multiply(BigDecimal.valueOf(this.quantidade));
    }

    // Getters e Setters
    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}