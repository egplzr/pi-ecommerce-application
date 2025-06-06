package com.senac.projetopi.ecommerceapi.dto;

/**
 * Apenas devolve o número de pedido gerado (por exemplo, “AB12CD34”), sem expor toda a entidade.
 */
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
