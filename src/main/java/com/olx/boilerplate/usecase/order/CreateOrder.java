package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.command.CreateOrderCommand;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CreateOrder {
    private final OrderRepository orderRepository;
    private final Counter ordersCreatedCounter;

    public CreateOrder(OrderRepository orderRepository, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.ordersCreatedCounter = meterRegistry.counter("orders.created");
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order execute(CreateOrderCommand command) {
        Order order = Order.create(command.getProduct(), command.getQuantity(), command.getPrice());
        Order saved = orderRepository.save(order);
        ordersCreatedCounter.increment();
        return saved;
    }
}
