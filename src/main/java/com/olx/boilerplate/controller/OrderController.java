package com.olx.boilerplate.controller;

import com.olx.boilerplate.annotation.ReadWriteTransaction;
import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.controller.dto.order.response.OrderResponse;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.usercase.order.CreateOrder;
import com.olx.boilerplate.usercase.order.DeleteOrder;
import com.olx.boilerplate.usercase.order.GetOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrder createOrder;
    private final GetOrder getOrder;
    private final DeleteOrder deleteOrder;

    public OrderController(CreateOrder createOrder, GetOrder getOrder, DeleteOrder deleteOrder) {
        this.createOrder = createOrder;
        this.getOrder = getOrder;
        this.deleteOrder = deleteOrder;
    }

    @ReadWriteTransaction
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        Order createdOrder = createOrder.execute(createOrderRequest);
        return buildCreatedResponse(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return getOrder.execute(id)
                .map(this::buildSuccessResponse)
                .orElse(buildNotFoundResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long id) {
        Optional<Order> orderToDelete = getOrder.execute(id);
        if (orderToDelete.isEmpty()) {
            return buildNotFoundResponse();
        }
        deleteOrder.execute(id);
        return buildSuccessResponse(orderToDelete.get());
    }

    private ResponseEntity<OrderResponse> buildCreatedResponse(Order order) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToOrderResponse(order));
    }

    private ResponseEntity<OrderResponse> buildSuccessResponse(Order order) {
        return ResponseEntity.ok()
                .body(mapToOrderResponse(order));
    }

    private ResponseEntity<OrderResponse> buildNotFoundResponse() {
        return ResponseEntity.notFound()
                .build();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.fromEntity(order);
    }
}
