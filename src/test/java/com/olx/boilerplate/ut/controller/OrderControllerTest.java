package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.OrderController;
import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.controller.dto.order.response.OrderResponse;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.usecase.order.CreateOrder;
import com.olx.boilerplate.usecase.order.DeleteOrder;
import com.olx.boilerplate.usecase.order.GetOrder;
import com.olx.boilerplate.usecase.order.ListOrders;
import com.olx.boilerplate.usecase.order.UpdateOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Mock
    private CreateOrder createOrder;

    @Mock
    private GetOrder getOrder;

    @Mock
    private UpdateOrder updateOrder;

    @Mock
    private DeleteOrder deleteOrder;

    @Mock
    private ListOrders listOrders;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(createOrder, getOrder, updateOrder, deleteOrder, listOrders);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        CreateOrderRequest request = new CreateOrderRequest("Item A", 2, 100.0);
        Order mockOrder = new Order(1L, "Item A", 2, 100.0);

        when(createOrder.execute(request.toCommand())).thenReturn(mockOrder);

        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrder.getId(), response.getBody().getId());
        verify(createOrder).execute(request.toCommand());
    }

    @Test
    void getOrder_ShouldReturnOrderDetails() {
        Long orderId = 1L;
        Order mockOrder = new Order(orderId, "Item B", 1, 50.0);

        when(getOrder.execute(orderId)).thenReturn(mockOrder);

        ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrder.getId(), response.getBody().getId());
        verify(getOrder).execute(orderId);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        Long orderId = 1L;
        doNothing().when(deleteOrder).execute(orderId);

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(deleteOrder).execute(orderId);
    }

    @Test
    void deleteOrder_ShouldPropagateNotFound() {
        Long orderId = 99L;
        doThrow(new ResourceNotFoundException("Order not found")).when(deleteOrder).execute(orderId);

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class,
                () -> orderController.deleteOrder(orderId));
    }
}
