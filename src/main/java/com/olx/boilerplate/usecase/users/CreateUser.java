package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.event.UserCreatedEvent;
import com.olx.boilerplate.domain.port.EventPublisher;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.command.CreateUserCommand;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateUser {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public CreateUser(UserRepository userRepository, EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User execute(CreateUserCommand command) {
        User user = User.createUser(command.getName(), command.getEmail());
        User saved = userRepository.save(user);
        eventPublisher.publishUserCreated(new UserCreatedEvent(saved.getId(), saved.getName(), saved.getEmail()));
        return saved;
    }
}
