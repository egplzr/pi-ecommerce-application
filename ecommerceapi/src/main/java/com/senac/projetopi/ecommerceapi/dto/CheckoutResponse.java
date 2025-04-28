package com.senac.projetopi.ecommerceapi.dto;

public class CheckoutResponse {
    private String numeroPedido;

    public CheckoutResponse(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }
}