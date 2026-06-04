package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class DeleteUser {
    private final UserRepository userRepository;

    public DeleteUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "users", allEntries = true)
    public void execute(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        userRepository.delete(userId);
    }
}
