package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class GetUser {
    private final UserRepository userRepository;

    public GetUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "users", key = "#userId")
    public User execute(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }
}
