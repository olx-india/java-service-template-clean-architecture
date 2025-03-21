package com.olx.boilerplate.usercase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetOrder {
    private final OrderRepository orderRepository;

    public Optional<Order> execute(Long id) {
        return orderRepository.findById(id);
    }
}
