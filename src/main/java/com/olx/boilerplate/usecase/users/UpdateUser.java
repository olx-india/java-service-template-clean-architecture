package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.usecase.users.command.UpdateUserCommand;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class UpdateUser {

    private final UserRepository userRepository;

    public UpdateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "users", allEntries = true)
    public User execute(UpdateUserCommand command) {
        User user = userRepository.findById(command.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + command.getId()));
        user.update(command.getName(), command.getEmail());
        return userRepository.save(user);
    }
}
