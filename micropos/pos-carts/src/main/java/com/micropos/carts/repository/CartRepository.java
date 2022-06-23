package com.micropos.carts.repository;

import com.micropos.carts.model.Cart;
import reactor.core.publisher.Mono;

public interface CartRepository {

    Mono<Cart> getCart(String name);

    Mono<Cart> putCart(String name, Cart cart);

}
