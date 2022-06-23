package com.micropos.carts.rest;

import com.micropos.carts.api.CartsApi;
import com.micropos.carts.dto.CartDto;
import com.micropos.carts.dto.OrderDto;
import com.micropos.carts.dto.ProductDto;
import com.micropos.carts.mapper.CartMapper;
import com.micropos.carts.model.Cart;
import com.micropos.carts.model.Item;
import com.micropos.carts.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
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
public class CartController implements CartsApi {

    private RestTemplate restTemplate;
    private CartService cartService;
    private CartMapper cartMapper;
    private CircuitBreakerFactory circuitBreakerFactory;
    private WebClient webClient;

    private static final String POS_PRODUCTS = "http://POS-PRODUCTS";
    private static final String POS_ORDER = "http://POS-ORDER";

    public CartController(WebClient.Builder webClientBuilder, CartService cartService, CartMapper cartMapper, RestTemplate restTemplate, CircuitBreakerFactory circuitBreakerFactory) {
        this.restTemplate = restTemplate;
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<ResponseEntity<CartDto>> addProduct(String username, String productId, ServerWebExchange exchange) {
        return cartService.save(getProduct(productId), username)
                .flatMap(b -> b ?
                        getCart(username, exchange) :
                        Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderDto>>> charge(String username, ServerWebExchange exchange) {
        return cartService.getCart(username)
                .map(cart -> {
                    Flux<OrderDto> orderDtoFlux = Flux.fromStream(cart.getItems().stream())
                            .map(item -> {
                                String url = POS_ORDER + "/api/order/" + username + "/" + item.getProduct().getId() + "/" + item.getQuantity();
                                OrderDto orderDto = new OrderDto();
                                orderDto = restTemplate.postForObject(url, orderDto, OrderDto.class);
                                return orderDto == null ? new OrderDto() : orderDto;
                            });
                    return cartService.emptyCart(username)
                            .flux()
                            .flatMap(b -> orderDtoFlux);
                })
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<CartDto>> decreaseProduct(String username, String productId, ServerWebExchange exchange) {
        return cartService.updateDecrease(getProduct(productId), username)
                .flatMap(b -> b ?
                        getCart(username, exchange) :
                        Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<CartDto>> deleteProduct(String username, String productId, ServerWebExchange exchange) {
        return cartService.remove(getProduct(productId), username)
                .flatMap(b -> b ?
                        getCart(username, exchange) :
                        Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<CartDto>> emptyCart(String username, ServerWebExchange exchange) {
        return cartService.emptyCart(username)
                .flatMap(b -> b ?
                        getCart(username, exchange) :
                        Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<CartDto>> getCart(String username, ServerWebExchange exchange) {
        return cartService.getCart(username)
                .map(cartMapper::toCartDto)
                .map(cart -> new ResponseEntity<>(cart, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<CartDto>> increaseProduct(String username, String productId, ServerWebExchange exchange) {
        return cartService.updateIncrement(getProduct(productId), username)
                .flatMap(b -> b ?
                        getCart(username, exchange) :
                        Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    private Mono<ProductDto> getProduct(String id) {
        return webClient.get()
                .uri(POS_PRODUCTS + "/api/products/" + id)
                .retrieve()
                .toEntity(ProductDto.class)
                .flatMap(resp -> resp.getStatusCode() == HttpStatus.OK ?
                        Mono.fromCallable(resp::getBody) :
                        Mono.empty());
    }
}
