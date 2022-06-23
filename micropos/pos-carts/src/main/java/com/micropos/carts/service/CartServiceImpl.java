package com.micropos.carts.service;

import com.micropos.carts.dto.ProductDto;
import com.micropos.carts.model.Cart;
import com.micropos.carts.model.Item;
import com.micropos.carts.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Mono<Boolean> update(Mono<ProductDto> productDto, int amount, String username) {
        return productDto.flatMap(product ->
                cartRepository.getCart(username)
                        .map(cart ->
                                cart.updateItem(new Item(product, amount)))
        );
    }

    @Override
    public Mono<Boolean> updateIncrement(Mono<ProductDto> productDto, String username) {
        return productDto.map(ProductDto::getId)
                .flatMap(id ->
                        cartRepository.getCart(username)
                                .map(cart -> cart.queryItemByProductId(id)))
                .map(Item::getQuantity)
                .flatMap(quantity -> update(productDto, quantity + 1, username));
    }

    @Override
    public Mono<Boolean> updateDecrease(Mono<ProductDto> productDto, String username) {
       return productDto.map(ProductDto::getId)
                .flatMap(id ->
                        cartRepository.getCart(username)
                                .map(cart -> cart.queryItemByProductId(id)))
                .map(Item::getQuantity)
                .flatMap(quantity -> quantity == 1 ?
                        remove(productDto, username) :
                        update(productDto, quantity - 1, username));
    }

    @Override
    public Mono<Boolean> remove(Mono<ProductDto> productDto, String username) {
        return productDto.map(ProductDto::getId)
                .flatMap(id ->
                        cartRepository.getCart(username)
                                .map(cart -> cart.deleteItem(cart.queryItemByProductId(id))));
    }

    @Override
    public Mono<Boolean> save(Mono<ProductDto> productDto, String username) {
        return productDto.map(product -> new Item(product, 1))
                .flatMap(item ->
                        cartRepository.getCart(username)
                                .map(cart -> cart.addItem(item)));
    }

    @Override
    public Mono<Boolean> emptyCart(String username) {
        return cartRepository.putCart(username, new Cart())
                .map(cart -> cart.getItems().size() == 0);
    }

    @Override
    public Mono<Cart> createCart(String username) {
        return cartRepository.putCart(username, new Cart());
    }

    @Override
    public Mono<Cart> getCart(String username) {
        return cartRepository.getCart(username);
    }
}
