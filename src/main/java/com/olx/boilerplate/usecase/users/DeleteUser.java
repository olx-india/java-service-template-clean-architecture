package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUser {

    private final UserRepository userRepository;

    public DeleteUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void execute(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        userRepository.delete(userId);
    }
}
