package com.olx.boilerplate.domain.event;

public final class OutboxEventTypes {

    public static final String USER_CREATED = "UserCreatedEvent";
    public static final String ORDER_CREATED = "OrderCreatedEvent";

    private OutboxEventTypes() {}
}
