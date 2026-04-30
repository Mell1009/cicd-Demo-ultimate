package com.demo.auction.service;

import com.demo.auction.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ProductService() {
        // Seed data
        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Vintage Guitar")
                .description("1967 Fender Stratocaster in excellent condition")
                .startingPrice(new BigDecimal("2500.00"))
                .category("Music")
                .active(true)
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Art Painting")
                .description("Oil on canvas, 20th century abstract")
                .startingPrice(new BigDecimal("800.00"))
                .category("Art")
                .active(true)
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Classic Watch")
                .description("Swiss mechanical movement, gold case")
                .startingPrice(new BigDecimal("1200.00"))
                .category("Jewelry")
                .active(false)
                .build());
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public List<Product> findActive() {
        return products.stream()
                .filter(Product::isActive)
                .toList();
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Product save(Product product) {
        product.setId(idCounter.getAndIncrement());
        products.add(product);
        return product;
    }

    public Optional<Product> update(Long id, Product updated) {
        return findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setStartingPrice(updated.getStartingPrice());
            existing.setCategory(updated.getCategory());
            existing.setActive(updated.isActive());
            return existing;
        });
    }

    public boolean delete(Long id) {
        return products.removeIf(p -> p.getId().equals(id));
    }
}
