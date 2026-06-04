package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.usecase.order.command.UpdateOrderCommand;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class UpdateOrder {

    private final OrderRepository orderRepository;

    public UpdateOrder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order execute(UpdateOrderCommand command) {
        Order order = orderRepository.findById(command.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + command.getId()));
        order.update(command.getProduct(), command.getQuantity(), command.getPrice());
        return orderRepository.save(order);
    }
}
