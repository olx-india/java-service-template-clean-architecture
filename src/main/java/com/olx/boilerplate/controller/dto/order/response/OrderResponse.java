package com.olx.boilerplate.controller.dto.order.response;

import com.olx.boilerplate.domain.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String product;
    private int quantity;
    private double price;

    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(order.getId(), order.getProduct(), order.getQuantity(), order.getPrice());
    }
}
