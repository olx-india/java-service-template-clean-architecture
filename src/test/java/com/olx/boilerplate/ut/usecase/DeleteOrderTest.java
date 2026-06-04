package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.DeleteOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DeleteOrder deleteOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order(orderId, "p", 1, 1.0)));
        doNothing().when(orderRepository).delete(orderId);

        deleteOrder.execute(orderId);

        verify(orderRepository, times(1)).delete(orderId);
    }

    @Test
    void testExecute_ShouldThrowWhenMissing() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deleteOrder.execute(2L));
    }
}
