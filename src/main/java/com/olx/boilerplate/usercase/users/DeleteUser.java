package com.olx.boilerplate.usercase.users;

import com.olx.boilerplate.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteUser {
    private final UserRepository userRepository;

    public DeleteUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Long userId) {
        userRepository.delete(userId);
    }
}
