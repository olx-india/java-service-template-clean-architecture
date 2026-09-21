package com.olx.boilerplate.domain.repository;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    PageResult<Order> findAll(PageQuery pageQuery);

    void delete(Long id);
}
