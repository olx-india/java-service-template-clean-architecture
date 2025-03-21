package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long id);

    void delete(Long id);
}
