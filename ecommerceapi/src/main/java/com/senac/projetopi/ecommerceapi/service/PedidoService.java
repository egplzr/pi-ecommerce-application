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
     * Finaliza um pedido para o cliente de ID passado.
     * @param clienteId  ID do cliente que está finalizando o pedido (obtido da sessão).
     * @param checkout   DTO enviado pelo frontend contendo endereço, forma de pagamento e lista de itens.
     * @return           DTO de resposta com o número do pedido gerado.
     */
    @Transactional
    public CheckoutResponse finalizarPedido(Long clienteId, CheckoutDTO checkout) {
        // 1) Verifica se o cliente existe e está ativo
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ValidacaoException("Cliente não encontrado ou não autenticado"));

        if (!cliente.isAtivo()) {
            throw new ValidacaoException("Conta de cliente inativa");
        }

        // 2) Cria novo objeto Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        // Gera número de pedido (pode usar UUID, timestamp ou sequência que você preferir)
        String numeroPedido = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        pedido.setNumeroPedido(numeroPedido);

        pedido.setDataCriacao(LocalDateTime.now());

        // Mapeia forma de pagamento
        pedido.setFormaPagamento(checkout.getformaPagamento());

        // Inicialmente, vamos deixar o status como AGUARDANDO_PAGAMENTO
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 3) Montar a lista de itens (PedidoItem) a partir do DTO de frontend
        List<PedidoItem> listaItens = checkout.getItens().stream()
                .map(this::converterItemDtoParaEntidade)
                .collect(Collectors.toList());

        // Define o relacionamento bidirecional: cada item aponta para o pedido
        listaItens.forEach(item -> item.setPedido(pedido));
        pedido.setItens(listaItens);

        // 4) Totalizar (somar preçoUnitario * quantidade)
        BigDecimal total = listaItens.stream()
                .map(PedidoItem::getPrecoUnitario)      // BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Observação: Se o DTO ItemDTO trouxer apenas precoUnitario e quantidade,
        //  seria .map(i -> i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
        BigDecimal totalSomado = listaItens.stream()
                .map(i -> i.getPrecoUnitario().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setTotal(totalSomado);

        // 5) Endereço (o campo na entidade Pedido é List<String> endereco)
        //    Como o frontend passou um String só, podemos colocá-lo em uma lista de um único elemento
        pedido.setEndereco(List.of(checkout.getEndereco()));

        // 6) Persistir no banco de dados (cascade salvará itens e endereços)
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 7) Retornar apenas o número do pedido (não devolvemos toda a entidade para evitar JSON muito grande/nested)
        return new CheckoutResponse(pedidoSalvo.getNumeroPedido());
    }

    private PedidoItem converterItemDtoParaEntidade(ItemDTO dto) {
        PedidoItem item = new PedidoItem();
        item.setProdutoId(dto.getProdutoId());
        item.setNome(dto.getNome());
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(BigDecimal.valueOf(dto.getPreco()));
        return item;
    }
}
