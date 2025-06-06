package com.senac.projetopi.ecommerceapi.service;

import com.senac.projetopi.ecommerceapi.dto.CheckoutDTO;
import com.senac.projetopi.ecommerceapi.dto.ItemDTO;
import com.senac.projetopi.ecommerceapi.dto.CheckoutResponse;
import com.senac.projetopi.ecommerceapi.exception.ValidacaoException;
import com.senac.projetopi.ecommerceapi.model.*;
import com.senac.projetopi.ecommerceapi.repository.PedidoRepository;
import com.senac.projetopi.ecommerceapi.repository.ClienteRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Finaliza um pedido para o cliente autenticado.
     */
    @Transactional
    public CheckoutResponse finalizarPedido(Long clienteId, CheckoutDTO checkout) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado ou não autenticado"));

        if (!cliente.isAtivo()) {
            throw new ValidacaoException("Conta de cliente inativa");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        String numeroPedido = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
        pedido.setNumeroPedido(numeroPedido);
        pedido.setDataCriacao(LocalDateTime.now());

        if (checkout.getFormaPagamento() == null || checkout.getFormaPagamento().isBlank()) {
            throw new ValidacaoException("Forma de pagamento não fornecida.");
        }
        pedido.setFormaPagamento(checkout.getFormaPagamento().toUpperCase());

        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        List<PedidoItem> listaItens = checkout.getItens().stream()
                .map(this::converterItemDtoParaEntidade)
                .collect(Collectors.toList());

        listaItens.forEach(item -> item.setPedido(pedido));
        pedido.setItens(listaItens);

        BigDecimal totalSomado = listaItens.stream()
                .map(i -> i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setTotal(totalSomado);

        if (checkout.getEndereco() == null || checkout.getEndereco().isBlank()) {
            throw new ValidacaoException("Endereço não fornecido.");
        }
        pedido.setEndereco(List.of(checkout.getEndereco()));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return new CheckoutResponse(pedidoSalvo.getNumeroPedido());
    }

    /**
     * Converte um DTO vindo do frontend em um PedidoItem.
     */
    private PedidoItem converterItemDtoParaEntidade(ItemDTO dto) {
        PedidoItem item = new PedidoItem();
        item.setProdutoId(dto.getProdutoId());
        item.setQuantidade(dto.getQuantidade());
        BigDecimal precoUni = BigDecimal.valueOf(dto.getPrecoUnitario());
        item.setPrecoUnitario(precoUni);
        item.setSubtotal(precoUni.multiply(BigDecimal.valueOf(dto.getQuantidade())));
        return item;
    }

    /**
     * Retorna todos os pedidos de um cliente, ordenados pela data de criação.
     */
    public List<Pedido> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findAllByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    /**
     * Retorna todos os pedidos do sistema (estoquista/administrador).
     */
    public List<Pedido> buscarTodosPedidos() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
    }

    /**
     * Atualiza o status de um pedido.
     */
    @Transactional
    public void atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ValidacaoException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
    }
}
