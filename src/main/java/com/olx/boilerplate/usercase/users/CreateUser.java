package com.olx.boilerplate.usercase.users;

import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateUser {
    private final UserRepository userRepository;

    public CreateUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(CreateUserRequest userRequest) {
        User user = User.createUser(userRequest.name, userRequest.email);
        return userRepository.save(user);
    }
}
