package com.olx.boilerplate.ut.usercase;

import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usercase.order.DeleteOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

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
        // Arrange
        Long orderId = 1L;
        doNothing().when(orderRepository).delete(orderId); // Mocking delete method

        // Act
        deleteOrder.execute(orderId);

        // Assert
        verify(orderRepository, times(1)).delete(orderId);
    }
}

