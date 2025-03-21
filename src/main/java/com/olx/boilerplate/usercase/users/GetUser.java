package com.olx.boilerplate.usercase.users;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GetUser {
    private final UserRepository userRepository;

    public GetUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long userId) {
        return userRepository.getById(userId);
    }
}
