package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.controller.dto.order.response.OrderResponse;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.usercase.order.CreateOrder;
import com.olx.boilerplate.usercase.order.DeleteOrder;
import com.olx.boilerplate.usercase.order.GetOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.olx.boilerplate.controller.OrderController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private CreateOrder createOrder;

    @Mock
    private GetOrder getOrder;

    @Mock
    private DeleteOrder deleteOrder;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(createOrder, getOrder, deleteOrder);
    }

    @Test
    void from() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("Item A", 2, 100.0);
        Order mockOrder = new Order(1L, "Item A", 2, 100.0);

        when(createOrder.execute(request)).thenReturn(mockOrder);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrder.getId(), response.getBody().getId());
        assertEquals(mockOrder.getProduct(), response.getBody().getProduct());
        assertEquals(mockOrder.getQuantity(), response.getBody().getQuantity());
        assertEquals(mockOrder.getPrice(), response.getBody().getPrice());

        verify(createOrder).execute(request);
    }

    @Test
    void getOrder_ShouldReturnOrderDetails() {
        // Given
        Long orderId = 1L;
        Order mockOrder = new Order(orderId, "Item B", 1, 50.0);

        when(getOrder.execute(orderId)).thenReturn(Optional.of(mockOrder));

        // When
        ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrder.getId(), response.getBody().getId());
        assertEquals(mockOrder.getProduct(), response.getBody().getProduct());
        assertEquals(mockOrder.getQuantity(), response.getBody().getQuantity());
        assertEquals(mockOrder.getPrice(), response.getBody().getPrice());

        verify(getOrder).execute(orderId);
    }

    @Test
    void getOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() {
        // Given
        Long orderId = 99L;
        when(getOrder.execute(orderId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(getOrder).execute(orderId);
    }

    @Test
    void deleteOrder_ShouldReturnDeletedOrder_WhenOrderExists() {
        // Given
        Long orderId = 1L;
        Order mockOrder = new Order(orderId, "Item C", 3, 150.0);

        when(getOrder.execute(orderId)).thenReturn(Optional.of(mockOrder));
        doNothing().when(deleteOrder).execute(orderId);

        // When
        ResponseEntity<OrderResponse> response = orderController.deleteOrder(orderId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrder.getId(), response.getBody().getId());

        verify(getOrder).execute(orderId);
        verify(deleteOrder).execute(orderId);
    }

    @Test
    void deleteOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() {
        // Given
        Long orderId = 99L;
        when(getOrder.execute(orderId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.deleteOrder(orderId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(getOrder).execute(orderId);
        verify(deleteOrder, never()).execute(anyLong());
    }
}

