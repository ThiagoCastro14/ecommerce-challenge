package com.ecommerce.repository.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Buscar todos os orders de um usuário específico
    List<Order> findByUsuario(User usuario);
}
