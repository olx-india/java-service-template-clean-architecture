package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.Order;
import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.repository.OrderRepository;
import com.olx.boilerplate.usecase.order.ListOrders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListOrdersTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ListOrders listOrders;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldDelegatePageQueryAndReturnPageResultUnchanged() {
        PageQuery pageQuery = PageQuery.of(0, 10);
        List<Order> orders = List.of(
                                     new Order(1L, "Keyboard", 2, 99.0),
                                     new Order(2L, "Monitor", 1, 349.0));
        PageResult<Order> expected = PageResult.of(orders, 0, 10, 2L);
        when(orderRepository.findAll(pageQuery)).thenReturn(expected);

        PageResult<Order> result = listOrders.execute(pageQuery);

        assertSame(expected, result);
        assertEquals(2, result.content().size());
        verify(orderRepository, times(1)).findAll(pageQuery);
    }

    @Test
    void testExecute_ShouldReturnEmptyPageResultWhenNoOrdersExist() {
        PageQuery pageQuery = PageQuery.of(2, 5);
        PageResult<Order> expected = PageResult.of(List.of(), 2, 5, 0L);
        when(orderRepository.findAll(pageQuery)).thenReturn(expected);

        PageResult<Order> result = listOrders.execute(pageQuery);

        assertSame(expected, result);
        assertEquals(0, result.content().size());
        verify(orderRepository, times(1)).findAll(pageQuery);
    }
}
