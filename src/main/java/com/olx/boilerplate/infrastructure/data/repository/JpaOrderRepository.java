package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.infrastructure.data.entities.OrderData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderData, Long> {
}
