package com.olx.boilerplate.controller;

import com.olx.boilerplate.annotation.ReadOnlyTransaction;
import com.olx.boilerplate.annotation.ReadWriteTransaction;
import com.olx.boilerplate.controller.dto.PageResponse;
import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.controller.dto.order.request.UpdateOrderRequest;
import com.olx.boilerplate.controller.dto.order.response.OrderResponse;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.usecase.order.CreateOrder;
import com.olx.boilerplate.usecase.order.DeleteOrder;
import com.olx.boilerplate.usecase.order.GetOrder;
import com.olx.boilerplate.usecase.order.ListOrders;
import com.olx.boilerplate.usecase.order.UpdateOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final CreateOrder createOrder;
    private final GetOrder getOrder;
    private final UpdateOrder updateOrder;
    private final DeleteOrder deleteOrder;
    private final ListOrders listOrders;

    public OrderController(CreateOrder createOrder, GetOrder getOrder, UpdateOrder updateOrder,
            DeleteOrder deleteOrder, ListOrders listOrders) {
        this.createOrder = createOrder;
        this.getOrder = getOrder;
        this.updateOrder = updateOrder;
        this.deleteOrder = deleteOrder;
        this.listOrders = listOrders;
    }

    @ReadWriteTransaction
    @PostMapping
    @Operation(summary = "Create an order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        Order createdOrder = createOrder.execute(createOrderRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(createdOrder));
    }

    @ReadOnlyTransaction
    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = getOrder.execute(id);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @ReadOnlyTransaction
    @GetMapping
    @Operation(summary = "List orders with pagination")
    public ResponseEntity<PageResponse<OrderResponse>> listOrders(Pageable pageable) {
        Page<Order> orders = listOrders.execute(pageable);
        return ResponseEntity.ok(new PageResponse<>(
                orders.getContent().stream().map(OrderResponse::fromEntity).toList(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages()));
    }

    @ReadWriteTransaction
    @PutMapping("/{id}")
    @Operation(summary = "Update an order")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest updateOrderRequest) {
        Order order = updateOrder.execute(updateOrderRequest.toCommand(id));
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @ReadWriteTransaction
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        deleteOrder.execute(id);
        return ResponseEntity.noContent().build();
    }
}
