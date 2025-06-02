package com.senac.projetopi.ecommerceapi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade que representa um pedido de compra.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número único do pedido (por exemplo, gerado com UUID curto).
     */
    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;

    /**
     * Data e hora de criação do pedido.
     */
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Valor total (soma de todos os itens) do pedido.
     */
    @Column(nullable = false)
    private BigDecimal total;

    /**
     * Status do pedido (enum: AGUARDANDO_PAGAMENTO, NO_PREPARO, ENVIADO, ENTREGUE, CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    /**
     * Endereço de entrega. Como a entidade mapeia List<String>, gravamos cada string como um elemento.
     * Neste cenário, estamos armazenando apenas um endereço (por questão de simplicidade),
     * mas pode-se posteriormente adicionar lógica para múltiplos endereços.
     */
    @ElementCollection
    @CollectionTable(
            name = "pedido_endereco",
            joinColumns = @JoinColumn(name = "pedido_id")
    )
    @Column(name = "endereco", nullable = false)
    private List<String> endereco;

    /**
     * Forma de pagamento (por exemplo, "BOLETO" ou "CARTAO").
     */
    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento;

    /**
     * Lista de itens que compõem este pedido. Cascade ALL para que, ao salvar o Pedido,
     * os PedidoItem associados sejam salvos automaticamente.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;

    /**
     * Cliente que fez este pedido. Chave estrangeira para a tabela clientes.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // ============================
    // Getters e Setters
    // ============================

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
