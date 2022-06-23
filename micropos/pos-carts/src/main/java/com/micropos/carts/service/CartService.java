package com.micropos.carts.service;

import com.micropos.carts.dto.ProductDto;
import com.micropos.carts.model.Cart;
import reactor.core.publisher.Mono;

public interface CartService {

    Mono<Boolean> update(Mono<ProductDto> productDto, int amount, String username);

    Mono<Boolean> updateIncrement(Mono<ProductDto> productDto, String username);

    Mono<Boolean> updateDecrease(Mono<ProductDto> productDto, String username);

    Mono<Boolean> remove(Mono<ProductDto> productDto, String username);

    Mono<Boolean> save(Mono<ProductDto> productDto, String username);

    Mono<Boolean> emptyCart(String username);

    Mono<Cart> createCart(String username);

    Mono<Cart> getCart(String username);

}
