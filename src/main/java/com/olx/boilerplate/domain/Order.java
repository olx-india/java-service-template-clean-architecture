package com.olx.boilerplate.domain;

import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Order {
    private Long id;
    private String product;
    private int quantity;
    private double price;

    public static Order from(CreateOrderRequest createOrderRequest) {
        return Order.builder()
                .product(createOrderRequest.getProduct())
                .quantity(createOrderRequest.getQuantity())
                .price(createOrderRequest.getPrice())
                .build();
    }
}
