package com.olx.boilerplate.ut.usercase;

import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usercase.order.CreateOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CreateOrder createOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        // Arrange
        CreateOrderRequest createOrderRequest = new CreateOrderRequest("ProductA", 2, 100.0);
        Order order = Order.from(createOrderRequest);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order savedOrder = createOrder.execute(createOrderRequest);

        // Assert
        assertNotNull(savedOrder);
        assertEquals("ProductA", savedOrder.getProduct());
        assertEquals(2, savedOrder.getQuantity());
        assertEquals(100.0, savedOrder.getPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
