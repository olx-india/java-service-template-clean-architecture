package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.infrastructure.data.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAUserRepository extends JpaRepository<UserData, Long> {
}
