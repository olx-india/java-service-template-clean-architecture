package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.GetOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrder getOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_WhenOrderExists() {
        Long orderId = 1L;
        Order order = new Order(orderId, "ProductA", 2, 100.0);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = getOrder.execute(orderId);

        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testExecute_WhenOrderDoesNotExist() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> getOrder.execute(2L));
    }
}
