package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class DeleteOrder {

    private final OrderRepository orderRepository;

    public DeleteOrder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public void execute(Long id) {
        if (orderRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        orderRepository.delete(id);
    }
}
