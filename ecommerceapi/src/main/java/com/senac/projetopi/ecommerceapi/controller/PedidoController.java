package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.CheckoutDTO;
import com.senac.projetopi.ecommerceapi.dto.CheckoutResponse;
import com.senac.projetopi.ecommerceapi.dto.ProdutoResponseDTO;
import com.senac.projetopi.ecommerceapi.model.Pedido;
import com.senac.projetopi.ecommerceapi.model.Produto;
import com.senac.projetopi.ecommerceapi.service.PedidoService;

import com.senac.projetopi.ecommerceapi.service.ProdutoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * POST /api/pedidos/finalizar
     *  Recebe um CheckoutDTO, verifica sessão do cliente e dispara a criação do pedido.
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarPedido(
            @RequestBody CheckoutDTO checkoutDTO,
            HttpServletRequest request) {

        // 1) Verificar sessão
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            CheckoutResponse resp = pedidoService.finalizarPedido(clienteId, checkoutDTO);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao finalizar pedido: " + e.getMessage());
        }
    }

    /**
     * GET /api/pedidos/cliente
     *  Lista todos os pedidos do cliente logado (usa pedidoService.buscarPedidosPorCliente).
     */
    @GetMapping("/cliente")
    public ResponseEntity<?> listarPedidosDoCliente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível obter seus pedidos. Tente novamente mais tarde.");
        }
    }

    @Controller
    @RequestMapping("/loja")
    public static class LojaPageController {

        @Autowired
        private ProdutoService produtoService;

        @GetMapping
        public String paginaInicial(
                @RequestParam(required = false) String nome,
                @RequestParam(required = false) String codigo,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "12") int size,
                Model model) {

            // Apenas produtos ativos são exibidos na loja
            Boolean ativo = true;

            Pageable pageable = PageRequest.of(page, size);
            Page<Produto> produtosPage = produtoService.buscarComFiltros(nome, codigo, ativo, pageable);

            // Converter para ProdutoResponseDTO
            Page<ProdutoResponseDTO> produtosDTO = produtosPage.map(ProdutoResponseDTO::fromEntity);

            model.addAttribute("produtos", produtosDTO);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", produtosPage.getTotalPages());
            model.addAttribute("filtroNome", nome);
            model.addAttribute("filtroCodigo", codigo);

            return "loja/home";
        }

        @GetMapping("/produto/{id}")
        public String paginaDetalhesProduto(@PathVariable Long id, Model model) {
            Produto produto = produtoService.buscarPorId(id);
            ProdutoResponseDTO dto = ProdutoResponseDTO.fromEntity(produto);
            model.addAttribute("produto", dto);
            return "loja/produto-detalhes";
        }

        // Pagina de Checkout
        @GetMapping("/checkout")
        public String checkoutPage() {
            return "loja/checkout";
        }

        // Página de confirmação de pedido
        @GetMapping("/confirmacao")
        public String confirmacaoPage(@RequestParam("numero") String numeroPedido, Model model) {
            model.addAttribute("numeroPedido", numeroPedido);
            return "loja/confirmacao";  // Retorna o arquivo confirmacao.html da pasta templates
        }

        // Página de Meus Pedidos
        @GetMapping("/meus-pedidos")
        public String meusPedidosPage() {
            return "loja/meus-pedidos";
        }

    }
}
