package com.micropos.delivery.repository;

import com.micropos.delivery.model.Delivery;
import reactor.core.publisher.Mono;

public interface DeliveryRepository {

    Mono<Delivery> queryDeliveryByOrderId(String orderId);

    void saveDelivery(Delivery delivery);

}
