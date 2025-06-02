package com.senac.projetopi.ecommerceapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade que representa um item dentro de um pedido.
 */
@Entity
@Table(name = "pedido_itens")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID do produto que está sendo pedido.
     * Não estamos mapeando relacionamento direto com a entidade Produto,
     * apenas armazenamos o ID e alguns dados de nome/preço unitário para facilitar.
     */
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    /**
     * Nome do produto (copiado no momento da criação do pedido, para histórico).
     */
    @Column(name = "nome", nullable = false)
    private String nome;

    /**
     * Quantidade deste item no pedido.
     */
    @Column(nullable = false)
    private int quantidade;

    /**
     * Preço unitário do produto no momento do pedido.
     */
    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    /**
     * Referência de volta para o Pedido ao qual este item pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ============================
    // Getters e Setters
    // ============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /**
     * Retorna o subtotal deste item (precoUnitario * quantidade).
     */
    public BigDecimal getSubtotal() {
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
    }
}
