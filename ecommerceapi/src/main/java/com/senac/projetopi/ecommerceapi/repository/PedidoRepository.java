package com.senac.projetopi.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senac.projetopi.ecommerceapi.model.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByDataCriacaoDesc();
    List<Pedido> findAllByClienteIdOrderByDataCriacaoDesc(Long clienteId);
}