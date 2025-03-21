package com.olx.boilerplate.usercase.order;

import com.olx.boilerplate.controller.dto.order.request.CreateOrderRequest;
import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrder {
    private final OrderRepository orderRepository;

    public Order execute(CreateOrderRequest createOrderRequest) {
        return orderRepository.save(Order.from(createOrderRequest));
    }
}
