package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long userId);

    PageResult<User> findAll(PageQuery pageQuery);

    void delete(Long userId);
}
