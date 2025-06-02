package com.senac.projetopi.ecommerceapi.repository;

import com.senac.projetopi.ecommerceapi.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
