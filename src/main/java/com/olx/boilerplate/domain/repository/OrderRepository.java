package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long id);

    Page<Order> findAll(Pageable pageable);

    void delete(Long id);
}
