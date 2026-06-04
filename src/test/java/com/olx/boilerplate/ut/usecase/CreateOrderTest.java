package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.CreateOrder;
import com.olx.boilerplate.usecase.order.command.CreateOrderCommand;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderTest {

    @Mock
    private OrderRepository orderRepository;

    private CreateOrder createOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createOrder = new CreateOrder(orderRepository, new SimpleMeterRegistry());
    }

    @Test
    void testExecute() {
        CreateOrderCommand command = new CreateOrderCommand("ProductA", 2, 100.0);
        Order order = Order.create(command.getProduct(), command.getQuantity(), command.getPrice());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = createOrder.execute(command);

        assertNotNull(savedOrder);
        assertEquals("ProductA", savedOrder.getProduct());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
