package com.micropos.products.service;

import com.micropos.products.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {


    Flux<Product> products();

    Mono<Product> getProduct(String id);

    Mono<Product> randomProduct();
}
