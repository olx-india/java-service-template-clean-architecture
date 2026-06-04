package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class GetOrder {
    private final OrderRepository orderRepository;

    public GetOrder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Cacheable(value = "orders", key = "#id")
    public Order execute(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }
}
