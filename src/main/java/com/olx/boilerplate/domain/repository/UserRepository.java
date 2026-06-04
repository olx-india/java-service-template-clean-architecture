package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long userId);

    Page<User> findAll(Pageable pageable);

    void delete(Long userId);
}
