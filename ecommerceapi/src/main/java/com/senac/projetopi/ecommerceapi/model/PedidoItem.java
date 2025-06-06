package com.senac.projetopi.ecommerceapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_itens")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID do produto referente a este item. */
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    /** Quantidade desse produto neste pedido */
    @Column(nullable = false)
    private Integer quantidade;

    /** Preço unitário ao qual se vendeu este item */
    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    /** Subtotal deste item, ou seja (preçoUnitário × quantidade) */
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    /**
     * Relacionamento MANY-TO-ONE → muitos itens pertencem a um pedido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // ========= Getters e Setters ========

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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
