package com.micropos.order.repository;

import com.micropos.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderRepository {

    Flux<Order> listOrder(String username);

    Mono<Order> queryOrderById(String username, String orderId);

    Mono<Order> saveOrder(String username, Order order);

}
