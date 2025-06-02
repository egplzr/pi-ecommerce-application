package com.senac.projetopi.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para receber no body do POST /api/pedidos/finalizar.
 * Contém o endereço, a forma de pagamento e a lista completa de itens do carrinho.
 */
public class FinalizarPedidoRequest {

    @NotBlank(message = "O endereço de entrega não pode ser vazio")
    private String endereco;

    @NotBlank(message = "A forma de pagamento não pode ser vazia")
    private String formaPagamento;

    @NotEmpty(message = "A lista de itens não pode estar vazia")
    private List<ItemCarrinhoDTO> itens;

    public FinalizarPedidoRequest() { }

    public FinalizarPedidoRequest(String endereco, String formaPagamento, List<ItemCarrinhoDTO> itens) {
        this.endereco = endereco;
        this.formaPagamento = formaPagamento;
        this.itens = itens;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemCarrinhoDTO> getItens() {
        return itens;
    }
    public void setItens(List<ItemCarrinhoDTO> itens) {
        this.itens = itens;
    }
}
