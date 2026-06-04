package com.olx.boilerplate.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreatedEvent {

    private final Long userId;
    private final String name;
    private final String email;
}
