package com.olx.boilerplate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String product;
    private int quantity;
    private double price;

    public static Order create(String product, int quantity, double price) {
        return Order.builder()
                        .product(product)
                        .quantity(quantity)
                        .price(price)
                        .build();
    }

    public Order update(String product, Integer quantity, Double price) {
        if (product != null) {
            this.product = product;
        }
        if (quantity != null) {
            this.quantity = quantity;
        }
        if (price != null) {
            this.price = price;
        }
        return this;
    }
}
