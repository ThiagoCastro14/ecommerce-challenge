package com.ecommerce.service.order;

import com.ecommerce.domain.order.*;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.repository.order.OrderItemRepository;
import com.ecommerce.repository.order.OrderRepository;
import com.ecommerce.repository.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Thiago")
                .email("thiago@email.com")
                .build();

        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Gaming Laptop")
                .price(new BigDecimal("5000.00"))
                .quantity(10)
                .build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Arrange
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order order = orderService.createOrder(user, List.of(item));

        // Assert
        assertNotNull(order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(new BigDecimal("10000.00"), order.getTotalValue());
        assertEquals(8, product.getQuantity()); // updated stock
    }

    @Test
    void shouldThrowErrorWhenCreatingOrderWithInsufficientStock() {
        // Arrange
        product.setQuantity(1);
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(user, List.of(item)));
    }

    @Test
    void shouldThrowErrorWhenCreatingEmptyOrder() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                orderService.createOrder(user, Collections.emptyList()));
    }

    @Test
    void shouldUpdateStatusToPaidSuccessfully() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.CREATED)
                .totalValue(new BigDecimal("500.00"))
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order updated = orderService.updateStatus(order.getId(), OrderStatus.PAID, user);

        // Assert
        assertEquals(OrderStatus.PAID, updated.getStatus());
    }

    @Test
    void shouldThrowErrorWhenUpdatingPaidOrderBackToCreated() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.PAID)
                .totalValue(new BigDecimal("100.00"))
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                orderService.updateStatus(order.getId(), OrderStatus.CREATED, user));
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.CREATED)
                .items(List.of(OrderItem.builder()
                        .product(product)
                        .quantity(2)
                        .build()))
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Order canceled = orderService.cancelOrder(order.getId(), user);

        // Assert
        assertEquals(OrderStatus.CANCELED, canceled.getStatus());
        assertEquals(12, product.getQuantity()); // stock restored
    }

    @Test
    void shouldThrowErrorWhenCancelingAlreadyPaidOrder() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .status(OrderStatus.PAID)
                .build();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                orderService.cancelOrder(order.getId(), user));
    }
}
