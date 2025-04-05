package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.ProdutoResponseDTO;
import com.senac.projetopi.ecommerceapi.model.Carrinho;
import com.senac.projetopi.ecommerceapi.model.CarrinhoItem;
import com.senac.projetopi.ecommerceapi.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CarrinhoService {

    @Autowired
    private ProdutoService produtoService;

    // Mapa para armazenar carrinhos por sessão
    private Map<String, Carrinho> carrinhos = new HashMap<>();

    public Carrinho getCarrinho(String sessionId) {
        return carrinhos.computeIfAbsent(sessionId, k -> new Carrinho());
    }

    public Carrinho adicionarItem(String sessionId, Long produtoId, int quantidade) {
        Carrinho carrinho = getCarrinho(sessionId);

        // Buscar produto pelo ID
        Produto produto = produtoService.buscarPorId(produtoId);

        // Verificar disponibilidade em estoque
        if (produto.getQuantidadeEstoque() < quantidade) {
            quantidade = produto.getQuantidadeEstoque(); // Limitar à quantidade disponível
        }

        if (quantidade <= 0) {
            return carrinho; // Retorna sem modificar se quantidade inválida
        }

        // Converter para DTO para obter a URL da imagem
        ProdutoResponseDTO produtoDTO = ProdutoResponseDTO.fromEntity(produto);

        // Criar item do carrinho
        CarrinhoItem item = new CarrinhoItem(
                produto.getId(),
                produto.getNome(),
                produto.getCodigo(),
                produto.getPreco(),
                produtoDTO.getImagemPrincipalUrl(),
                quantidade
        );

        // Adicionar ao carrinho
        carrinho.adicionarItem(item);

        return carrinho;
    }

    public Carrinho removerItem(String sessionId, Long produtoId) {
        Carrinho carrinho = getCarrinho(sessionId);
        carrinho.removerItem(produtoId);
        return carrinho;
    }

    public Carrinho atualizarQuantidade(String sessionId, Long produtoId, int quantidade) {
        Carrinho carrinho = getCarrinho(sessionId);

        // Verificar estoque disponível
        Produto produto = produtoService.buscarPorId(produtoId);
        if (quantidade > produto.getQuantidadeEstoque()) {
            quantidade = produto.getQuantidadeEstoque();
        }

        carrinho.atualizarQuantidade(produtoId, quantidade);
        return carrinho;
    }

    public Carrinho calcularFrete(String sessionId, String cep) {
        Carrinho carrinho = getCarrinho(sessionId);

        // Implementação simplificada de cálculo de frete
        BigDecimal valorTotal = carrinho.getValorTotal();
        BigDecimal valorFrete;

        // Frete grátis para compras acima de R$ 100
        if (valorTotal.compareTo(new BigDecimal("100.00")) >= 0) {
            valorFrete = BigDecimal.ZERO;
        } else {
            // Frete fixo de R$ 10
            valorFrete = new BigDecimal("10.00");
        }

        carrinho.setValorFrete(valorFrete);
        return carrinho;
    }

    public void limparCarrinho(String sessionId) {
        Carrinho carrinho = getCarrinho(sessionId);
        carrinho.limpar();
    }
}