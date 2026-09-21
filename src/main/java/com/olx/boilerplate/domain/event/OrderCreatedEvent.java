package com.olx.boilerplate.domain.event;

public record OrderCreatedEvent(Long orderId, String product, int quantity, double price) {
}
