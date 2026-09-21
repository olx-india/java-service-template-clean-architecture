package com.olx.boilerplate.usecase.users;

import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ListUsers {

    private final UserRepository userRepository;

    public ListUsers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PageResult<User> execute(PageQuery pageQuery) {
        return userRepository.findAll(pageQuery);
    }
}
