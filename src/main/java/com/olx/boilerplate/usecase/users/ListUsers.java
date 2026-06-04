package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ListUsers {

    private final UserRepository userRepository;

    public ListUsers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> execute(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
