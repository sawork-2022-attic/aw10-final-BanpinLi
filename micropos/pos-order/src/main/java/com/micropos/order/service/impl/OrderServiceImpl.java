package com.micropos.order.service.impl;

import com.micropos.order.model.Order;
import com.micropos.order.repository.OrderRepository;
import com.micropos.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Flux<Order> getOrderList(String username) {
        return orderRepository.listOrder(username);
    }

    @Override
    public Mono<Order> getOrder(String username, String orderId) {
        return orderRepository.queryOrderById(username, orderId);
    }

    @Override
    public Mono<Order> saveOrder(String username, Order order) {
        return orderRepository.saveOrder(username, order);
    }
}
