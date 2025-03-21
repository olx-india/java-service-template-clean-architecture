package com.olx.boilerplate.infrastructure.data.entities;

import com.olx.boilerplate.domain.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_order")
public class OrderData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String product;
    private int quantity;
    private double price;

    public static OrderData from(Order order) {
        return OrderData.builder()
                .id(order.getId())
                .product(order.getProduct())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .build();
    }

    public Order fromThis() {
        return Order.builder()
                .id(id)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
