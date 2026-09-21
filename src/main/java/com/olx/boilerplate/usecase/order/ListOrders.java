package com.olx.boilerplate.usecase.order;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class ListOrders {

    private final OrderRepository orderRepository;

    public ListOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PageResult<Order> execute(PageQuery pageQuery) {
        return orderRepository.findAll(pageQuery);
    }
}
