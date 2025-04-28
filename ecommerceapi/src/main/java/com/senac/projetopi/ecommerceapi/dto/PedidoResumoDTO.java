package com.senac.projetopi.ecommerceapi.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoResumoDTO {
    private String numero;
    private LocalDateTime data;
    private BigDecimal total;
    private String status;

    public PedidoResumoDTO(String numero, LocalDateTime data, BigDecimal total, String status) {
        this.numero = numero;
        this.data = data;
        this.total = total;
        this.status = status;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDateTime getData() {
        return data;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}

