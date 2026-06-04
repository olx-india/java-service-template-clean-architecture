package com.olx.boilerplate.domain.port;

import com.olx.boilerplate.domain.event.UserCreatedEvent;

public interface EventPublisher {

    void publishUserCreated(UserCreatedEvent event);
}
