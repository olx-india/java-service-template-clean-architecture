package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.event.OrderCreatedEvent;
import com.olx.boilerplate.domain.port.EventPublisher;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.command.CreateOrderCommand;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateOrder {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final Counter ordersCreatedCounter;

    public CreateOrder(OrderRepository orderRepository, EventPublisher eventPublisher, MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.ordersCreatedCounter = meterRegistry.counter("orders.created");
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order execute(CreateOrderCommand command) {
        Order order = Order.create(command.getProduct(), command.getQuantity(), command.getPrice());
        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCreated(
                                           new OrderCreatedEvent(saved.getId(), saved.getProduct(), saved.getQuantity(),
                                                                 saved.getPrice()));
        ordersCreatedCounter.increment();
        return saved;
    }
}
