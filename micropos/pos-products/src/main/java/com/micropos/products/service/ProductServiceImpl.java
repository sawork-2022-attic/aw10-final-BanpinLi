package com.micropos.products.service;

import com.micropos.products.model.Product;
import com.micropos.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private Random random;

    public ProductServiceImpl(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.random = new Random();
    }

    @Override
    public Flux<Product> products() {
        return productRepository.allProducts();
    }

    @Override
    public Mono<Product> getProduct(String id) {
        return productRepository.findProduct(id);
    }

    @Override
    public Mono<Product> randomProduct() {
        return productRepository.getProductByIndex(random.nextInt(7000));
    }
}
