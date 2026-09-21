package com.olx.boilerplate.infrastructure.data.repository;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.infrastructure.data.entities.OrderData;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderData orderData = OrderData.from(order);
        return jpaOrderRepository.save(orderData).fromThis();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id).map(OrderData::fromThis);
    }

    @Override
    public PageResult<Order> findAll(PageQuery pageQuery) {
        var page = jpaOrderRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return PageResult.of(page.getContent().stream().map(OrderData::fromThis).toList(), page.getNumber(),
                             page.getSize(), page.getTotalElements());
    }

    @Override
    public void delete(Long id) {
        jpaOrderRepository.deleteById(id);
    }
}
