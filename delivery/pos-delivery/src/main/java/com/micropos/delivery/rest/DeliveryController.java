package com.micropos.delivery.rest;

import com.micropos.delivery.api.DeliveryApi;
import com.micropos.delivery.dto.DeliveryDto;
import com.micropos.delivery.dto.OrderDto;
import com.micropos.delivery.mapper.DeliveryMapper;
import com.micropos.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class DeliveryController implements DeliveryApi {

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public Mono<ResponseEntity<DeliveryDto>> getDelivery(String orderId, ServerWebExchange exchange) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(e -> new ResponseEntity<>(deliveryMapper.toDeliveryDto(e), HttpStatus.OK));
    }

    @PostMapping("/delivery")
    public void add(@RequestBody OrderDto orderDto) {
        deliveryService.createDelivery(orderDto);
    }

}
