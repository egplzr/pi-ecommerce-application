package com.senac.projetopi.ecommerceapi.dto;

import java.util.List;

public class CheckoutDTO {
    private List<ItemDTO> itens;
    private String endereco;          // Ex: "Rua X, 123, Bairro Y, Cidade Z - CEP: 00000-000"
    private String formaPagamento;    // renomeado para bater com o JSON enviado pelo front‐end

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


    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getformaPagamento() {
        return formaPagamento;
    }
}
