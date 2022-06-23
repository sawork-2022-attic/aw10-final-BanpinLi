package com.micropos.order.rest;

import com.micropos.order.api.OrderApi;
import com.micropos.order.dto.OrderDto;
import com.micropos.order.dto.ProductDto;
import com.micropos.order.mapper.OrderMapper;
import com.micropos.order.model.Order;
import com.micropos.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController implements OrderApi {

    private OrderService orderService;
    private OrderMapper orderMapper;
    private StreamBridge streamBridge;

    private WebClient webClient;

    public OrderController(OrderService orderService, OrderMapper orderMapper, StreamBridge streamBridge, WebClient.Builder webClientBuilder) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.streamBridge = streamBridge;
        this.webClient = webClientBuilder.build();
    }

    private static final String POS_PRODUCTS = "http://POS-PRODUCTS/";

    @Override
    public Mono<ResponseEntity<OrderDto>> addOrder(String username, String productId, Integer quantity, ServerWebExchange exchange) {
        return webClient.get()
                .uri(POS_PRODUCTS + "/api/products/" + productId)
                .retrieve()
                .toEntity(ProductDto.class)
                .flatMap(entity -> {
                    System.out.println(entity);
                    if(entity.getStatusCode() == HttpStatus.OK) {
                        Order order = new Order();
                        order.setProduct(entity.getBody());
                        order.setQuantity(quantity);
                        return orderService.saveOrder(username, order)
                                .map(orderMapper::toOrderDto)
                                .flatMap(e -> {
                                    streamBridge.send("orderDelivery", e);
                                    return Mono.fromCallable(() -> new ResponseEntity<>(e, HttpStatus.OK));
                                });
                    } else {
                        return Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> getOrder(String username, String orderId, ServerWebExchange exchange) {
        return orderService.getOrder(username, orderId)
                .map(e -> new ResponseEntity<>(orderMapper.toOrderDto(e), HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderDto>>> getOrders(String username, ServerWebExchange exchange) {
        return Mono.fromCallable(() ->
                new ResponseEntity<>(
                        orderService.getOrderList(username)
                                .map(e -> orderMapper.toOrderDto(e)), HttpStatus.OK
                ));
    }


//    @Override
//    public ResponseEntity<OrderDto> addOrder(String username, String productId, Integer quantity) {
//        ProductDto productDto = restTemplate.getForObject(POS_PRODUCTS + "api/products/" + productId, ProductDto.class);
//        if(productDto == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        Order order = new Order();
//        order.setProduct(productDto);
//        order.setQuantity(quantity);
//        order = orderService.saveOrder(username, order);
//
//        // 将订单放入到消息队列，让delivery模块进行处理
//        streamBridge.send("orderDelivery", orderMapper.toOrderDto(order));
//
//        return new ResponseEntity<>(orderMapper.toOrderDto(order), HttpStatus.OK);
//    }

//    @Override
//    public ResponseEntity<OrderDto> getOrder(String username, String orderId) {
//        Order order = orderService.getOrder(username, orderId);
//        if(order == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(orderMapper.toOrderDto(order), HttpStatus.OK);
//    }

//    @Override
//    public ResponseEntity<List<OrderDto>> getOrders(String username) {
//        List<Order> orderList = orderService.getOrderList(username);
//        if(orderList == null || orderList.size() == 0) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        List<OrderDto> orderDtoList = new ArrayList<>();
//        orderList.forEach(o -> orderDtoList.add(orderMapper.toOrderDto(o)));
//        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
//    }
}
