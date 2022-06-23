package com.micropos.products.repository;

import com.micropos.products.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class AmazonRepository implements ProductRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Flux<Product> allProducts() {
        String sql = "select id, name, price, image from t_product";
        return Flux.fromStream(
                jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class))
                        .stream()
        );
    }

    @Override
    public Mono<Product> findProduct(String productId) {
        String sql = "select id, name, price, image from t_product where id=?";
        return Mono.fromCallable(() -> {
            List<Product> productList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class), productId);
            return productList.size() == 0 ? null : productList.get(0);
        });
    }
}
