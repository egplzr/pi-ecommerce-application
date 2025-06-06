package com.senac.projetopi.ecommerceapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa um Pedido no banco.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número único do pedido, gerado no back-end (p. ex. UUID curto). */
    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;

    /** Data/hora de criação do pedido. */
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    /** Valor total do pedido (soma de todos os itens). */
    @Column(nullable = false)
    private BigDecimal total;

    /** Status do pedido. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    /**
     * Endereço de entrega.
     * No front-end mandamos apenas UMA string completa, mas armazenamos aqui
     * como ElementCollection <String> para a simplicidade da solução.
     */
    @ElementCollection
    @CollectionTable(
            name = "pedido_endereco",
            joinColumns = @JoinColumn(name = "pedido_id")
    )
    @Column(name = "endereco", nullable = false)
    private List<String> endereco;

    /** “BOLETO” ou “CARTAO” */
    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento;

    /**
     * Itens que fazem parte deste pedido.
     * Cascade ALL → ao salvar Pedido, seus itens também são salvos automaticamente.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;

    /** O cliente que fez este pedido */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ================= Getters e Setters =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public List<String> getEndereco() {
        return endereco;
    }

    public void setEndereco(List<String> endereco) {
        this.endereco = endereco;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItem> itens) {
        this.itens = itens;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
