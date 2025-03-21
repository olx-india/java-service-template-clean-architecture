package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User save(User user);

    User getById(Long userId);

    void delete(Long userId);
}
