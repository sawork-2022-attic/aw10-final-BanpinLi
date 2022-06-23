package com.micropos.order.service;

import com.micropos.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    Flux<Order> getOrderList(String username);

    Mono<Order> getOrder(String username, String orderId);

    Mono<Order> saveOrder(String username, Order order);

}
