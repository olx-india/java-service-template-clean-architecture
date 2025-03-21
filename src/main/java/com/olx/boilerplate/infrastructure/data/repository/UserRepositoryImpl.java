package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.infrastructure.data.entities.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JPAUserRepository jpaUserRepository;

    @Autowired
    public UserRepositoryImpl(JPAUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(UserData.from(user)).fromThis();
    }

    @Override
    public User getById(Long userId) {
        return jpaUserRepository.getById(userId).fromThis();
    }

    @Override
    public void delete(Long userId) {
        jpaUserRepository.deleteById(userId);
    }
}
