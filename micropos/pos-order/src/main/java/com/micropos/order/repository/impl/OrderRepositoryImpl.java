package com.micropos.order.repository.impl;

import com.micropos.order.model.Order;
import com.micropos.order.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private static final List<Order> EMPTY_LIST = new ArrayList<>();
    private ConcurrentHashMap<String, List<Order>> map = new ConcurrentHashMap<>();
    private long id = Long.MAX_VALUE >> 22;

    @Override
    public Flux<Order> listOrder(String username) {
        return Flux.fromStream(map.getOrDefault(username, EMPTY_LIST).stream());
    }

    @Override
    public Mono<Order> queryOrderById(String username, String orderId) {
        return Mono.fromCallable(() -> {
            List<Order> orderList = map.getOrDefault(username, EMPTY_LIST)
                    .stream()
                    .filter(e -> e.getOrderId().equals(orderId))
                    .collect(Collectors.toList());
            return orderList.isEmpty() ? null : orderList.get(0);
        });
    }

    @Override
    public Mono<Order> saveOrder(String username, Order order) {
        return Mono.fromCallable(() -> {
            List<Order> orderList = map.computeIfAbsent(username, k -> new ArrayList<>());
            orderList.add(order);
            order.setOrderId(orderIdIncrement());
            return order;
        });
    }

    private String orderIdIncrement() {
        synchronized (OrderRepositoryImpl.class) {
            id++;
        }
        return Long.toString(id);
    }
}
