package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.dto.CheckoutDTO;
import com.senac.projetopi.ecommerceapi.dto.CheckoutResponse;
import com.senac.projetopi.ecommerceapi.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Recebe um CheckoutDTO no corpo da requisição, cria o pedido no banco
     * e retorna um CheckoutResponse com o número do pedido.
     *
     * Path: POST /api/pedidos/finalizar
     */
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarPedido(
            @RequestBody CheckoutDTO checkoutDTO,
            HttpServletRequest request) {

        // 1) Verificar se o cliente está autenticado (busca pelo atributo de sessão)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            // Usuário não está autenticado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado");
        }

        Long clienteId = (Long) session.getAttribute("clienteId");

        try {
            CheckoutResponse resp = pedidoService.finalizarPedido(clienteId, checkoutDTO);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            // Se for erro de validação, devolvemos bad request e mensagem
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
