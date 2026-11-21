package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.UpdateOrder;
import com.olx.boilerplate.usecase.order.command.UpdateOrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateOrderTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private UpdateOrder updateOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldUpdateAndSaveOrderWhenFound() {
        Long orderId = 1L;
        Order existing = new Order(orderId, "OldProduct", 2, 50.0);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, "NewProduct", 5, 79.5);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        Order result = updateOrder.execute(command);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("NewProduct", result.getProduct());
        assertEquals(5, result.getQuantity());
        assertEquals(79.5, result.getPrice());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(existing);
    }

    @Test
    void testExecute_ShouldPreserveUnchangedFieldsWhenNullInCommand() {
        Long orderId = 1L;
        Order existing = new Order(orderId, "ProductA", 3, 25.0);
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, null, 7, null);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        Order result = updateOrder.execute(command);

        assertEquals("ProductA", result.getProduct());
        assertEquals(7, result.getQuantity());
        assertEquals(25.0, result.getPrice());
        verify(orderRepository, times(1)).save(existing);
    }

    @Test
    void testExecute_ShouldThrowWhenOrderMissingAndNeverSave() {
        Long orderId = 42L;
        UpdateOrderCommand command = new UpdateOrderCommand(orderId, "NewProduct", 1, 10.0);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> updateOrder.execute(command));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any());
    }
}
