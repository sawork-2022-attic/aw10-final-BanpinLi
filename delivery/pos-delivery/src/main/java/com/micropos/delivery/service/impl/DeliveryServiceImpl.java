package com.micropos.delivery.service.impl;

import com.micropos.delivery.dto.OrderDto;
import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.repository.DeliveryRepository;
import com.micropos.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Override
    public Mono<Delivery> getDeliveryByOrderId(String orderId) {
        return deliveryRepository.queryDeliveryByOrderId(orderId);
    }

    @Override
    public void createDelivery(OrderDto order) {
        Delivery delivery = new Delivery(order, "您的包裹正在运输中...");
        deliveryRepository.saveDelivery(delivery);
    }
}
