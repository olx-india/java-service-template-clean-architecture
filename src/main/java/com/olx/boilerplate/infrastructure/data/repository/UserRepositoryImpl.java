package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.infrastructure.data.entities.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JPAUserRepository jpaUserRepository;

    public UserRepositoryImpl(JPAUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(UserData.from(user)).fromThis();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaUserRepository.findById(userId).map(UserData::fromThis);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable).map(UserData::fromThis);
    }

    @Override
    public void delete(Long userId) {
        jpaUserRepository.deleteById(userId);
    }
}
