package com.ecommerce.repository.order;

import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    // Buscar todos os itens de um order específico
    List<OrderItem> findByOrder(Order order);
}

