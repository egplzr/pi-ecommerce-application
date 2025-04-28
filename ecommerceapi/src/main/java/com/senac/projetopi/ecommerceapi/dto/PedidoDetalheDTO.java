package com.senac.projetopi.ecommerceapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDetalheDTO {
    private String numero;
    private LocalDateTime data;
    private BigDecimal total;
    private String status;
    private String endereco;
    private String pagamento;
    private List<ItemDTO> itens;

    public PedidoDetalheDTO(String numero, LocalDateTime data, BigDecimal total,
                            String status, String endereco, String pagamento, List<ItemDTO> itens) {
        this.numero = numero;
        this.data = data;
        this.total = total;
        this.status = status;
        this.endereco = endereco;
        this.pagamento = pagamento;
        this.itens = itens;
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

    public String getEndereco() {
        return endereco;
    }

    public String getPagamento() {
        return pagamento;
    }

    public List<ItemDTO> getItens() {
        return itens;
    }
}
