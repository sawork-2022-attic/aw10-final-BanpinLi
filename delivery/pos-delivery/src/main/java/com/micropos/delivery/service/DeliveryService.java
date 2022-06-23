package com.micropos.delivery.service;

import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.dto.OrderDto;
import reactor.core.publisher.Mono;

public interface DeliveryService {

    Mono<Delivery> getDeliveryByOrderId(String orderId);

    void createDelivery(OrderDto order);

}
