package com.micropos.carts.repository;

import com.micropos.carts.model.Cart;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private Map<String, Cart> rep = new HashMap<>();

    @Override
    public Mono<Cart> getCart(String username) {
        return Mono.fromCallable(() -> {
            Cart cart = rep.get(username);
            if(cart == null) {
                cart = new Cart();
                rep.put(username, cart);
            }
            return cart;
        });
    }

    @Override
    public Mono<Cart> putCart(String username, Cart cart) {
        return Mono.fromCallable(() -> rep.put(username, cart));
    }
}
