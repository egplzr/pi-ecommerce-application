package com.senac.projetopi.ecommerceapi.controller;

import com.senac.projetopi.ecommerceapi.model.Pedido;
import com.senac.projetopi.ecommerceapi.model.StatusPedido;
import com.senac.projetopi.ecommerceapi.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/loja/pedidos")
public class PedidoEstoquistaController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarPedidos(Model model, HttpServletRequest request) {
        System.out.println(">> ENTROU NO MÉTODO listarPedidos() <<");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            return "redirect:/login";
        }

        List<Pedido> todos = pedidoService.buscarTodosPedidos();
        model.addAttribute("pedidos", todos);
        model.addAttribute("statusValores", StatusPedido.values());
        return "loja/pedidos";
    }

    @PostMapping("/atualizar-status")
    public String atualizarStatus(
            @RequestParam("pedidoId") Long pedidoId,
            @RequestParam("novoStatus") String statusString,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            return "redirect:/login";
        }

        try {
            StatusPedido novoStatus = StatusPedido.valueOf(statusString);
            pedidoService.atualizarStatus(pedidoId, novoStatus);
        } catch (IllegalArgumentException ex) {
            return "redirect:/loja/pedidos?erro=invalidStatus";
        }

        return "redirect:/loja/pedidos";
    }
}
