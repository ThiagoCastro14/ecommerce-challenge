package com.ecommerce.controller.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.user.User;
import com.ecommerce.service.order.OrderService;
import com.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Controlador responsável pelos endpoints de orders.
 * Todos os endpoints são protegidos (usuário autenticado via JWT).
 */

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<OrderItem> items) {
        User user = userService.getAuthenticatedUser();
        Order order = orderService.createOrder(user, items);
        return ResponseEntity.ok(order);
    }

      /**
     * Lista todos os orders do usuário logado.
     */

    @GetMapping
    public ResponseEntity<List<Order>> listMyOrders() {
        User user = userService.getAuthenticatedUser();
        List<Order> orders = orderService.listOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

     /**
     * Busca um order específico pelo ID (somente se for do usuário logado).
     */

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Order order = orderService.findById(id, user);
        return ResponseEntity.ok(order);
    }

     /**
     * Atualiza o status de um order.
     * Exemplo: PUT /orders/{id}/status?novoStatus=PAGO
     */

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus newStatus
    ) {
        User user = userService.getAuthenticatedUser();
        Order order = orderService.updateStatus(id, newStatus, user);
        return ResponseEntity.ok(order);
    }

       /**
     * Cancela um order, se ainda estiver no status CRIADO.
     */

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable UUID id) {
        User user = userService.getAuthenticatedUser();
        Order order = orderService.cancelOrder(id, user);
        return ResponseEntity.ok(order);
    }
}
