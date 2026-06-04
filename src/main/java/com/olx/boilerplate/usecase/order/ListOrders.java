package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ListOrders {

    private final OrderRepository orderRepository;

    public ListOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> execute(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
