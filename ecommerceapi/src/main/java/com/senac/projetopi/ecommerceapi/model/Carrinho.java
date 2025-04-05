package com.senac.projetopi.ecommerceapi.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrinho {
    private List<CarrinhoItem> itens = new ArrayList<>();
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private BigDecimal valorFrete = BigDecimal.ZERO;

    public Carrinho() {
    }

    public void adicionarItem(CarrinhoItem item) {
        Optional<CarrinhoItem> itemExistente = itens.stream()
                .filter(i -> i.getProdutoId().equals(item.getProdutoId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            CarrinhoItem existente = itemExistente.get();
            existente.setQuantidade(existente.getQuantidade() + item.getQuantidade());
        } else {
            itens.add(item);
        }
        calcularTotal();
    }

    public void removerItem(Long produtoId) {
        itens.removeIf(item -> item.getProdutoId().equals(produtoId));
        calcularTotal();
    }

    public void atualizarQuantidade(Long produtoId, int quantidade) {
        itens.stream()
                .filter(item -> item.getProdutoId().equals(produtoId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantidade <= 0) {
                        removerItem(produtoId);
                    } else {
                        item.setQuantidade(quantidade);
                        calcularTotal();
                    }
                });
    }

    public void calcularTotal() {
        this.valorTotal = itens.stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void limpar() {
        itens.clear();
        valorTotal = BigDecimal.ZERO;
        valorFrete = BigDecimal.ZERO;
    }

    // Getters e Setters
    public List<CarrinhoItem> getItens() {
        return itens;
    }

    public void setItens(List<CarrinhoItem> itens) {
        this.itens = itens;
        calcularTotal();
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getValorTotalComFrete() {
        return valorTotal.add(valorFrete);
    }

    public int getQuantidadeItens() {
        return itens.stream().mapToInt(CarrinhoItem::getQuantidade).sum();
    }
}