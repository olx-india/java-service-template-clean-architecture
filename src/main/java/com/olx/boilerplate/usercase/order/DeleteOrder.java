package com.olx.boilerplate.usercase.order;

import com.olx.boilerplate.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class DeleteOrder {
    private final OrderRepository orderRepository;

    public void execute(Long id) {
        orderRepository.delete(id);
    }
}
