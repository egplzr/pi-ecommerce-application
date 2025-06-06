package com.senac.projetopi.ecommerceapi.dto;

import java.util.List;

/**
 * DTO enviado pelo front-end na hora do checkout.
 * Deve conter:
 *   - lista de itens (ItemDTO),
 *   - um único endereço como String,
 *   - forma de pagamento como String.
 */
public class CheckoutDTO {
    private List<ItemDTO> itens;
    private String endereco;      // p.ex.: "Rua X, 123, Bairro Y, Cidade Z - CEP: 00000-000"
    private String formaPagamento; // "BOLETO" ou "CARTAO"

    // Getters e Setters

    public List<ItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemDTO> itens) {
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
}
