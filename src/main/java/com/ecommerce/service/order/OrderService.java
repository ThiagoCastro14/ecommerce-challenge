package com.ecommerce.service.order;

import com.ecommerce.domain.order.*;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.repository.order.OrderItemRepository;
import com.ecommerce.repository.order.OrderRepository;
import com.ecommerce.repository.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(User user, List<OrderItem> itemsRequest) {
        if (itemsRequest == null || itemsRequest.isEmpty())
            throw new IllegalArgumentException("The order must contain at least one item.");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalValue = BigDecimal.ZERO;

        for (OrderItem itemRequest : itemsRequest) {
            Product product = productRepository.findById(itemRequest.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemRequest.getProduct().getId()));

            if (product.getQuantity() < itemRequest.getQuantity())
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());

            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            order.addItem(item);

            totalValue = totalValue.add(item.getSubtotal());
        }

        order.setTotalValue(totalValue);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findById(UUID id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId()))
            throw new SecurityException("You are not allowed to access this order.");
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> listOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Transactional
    public Order updateStatus(UUID orderId, OrderStatus newStatus, User user) {
        Order order = findById(orderId, user);

        if (order.getStatus() == OrderStatus.CANCELED)
            throw new IllegalStateException("Canceled orders cannot be modified.");

        if (order.getStatus() == OrderStatus.PAID && newStatus == OrderStatus.CREATED)
            throw new IllegalStateException("Cannot revert a paid order back to CREATED.");

        if (newStatus == OrderStatus.PAID && order.getTotalValue().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("Cannot mark as paid an order with total value 0.");

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(UUID orderId, User user) {
        Order order = findById(orderId, user);

        if (order.getStatus() != OrderStatus.CREATED)
            throw new IllegalStateException("Only orders in CREATED status can be canceled.");

        order.setStatus(OrderStatus.CANCELED);

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }
}
