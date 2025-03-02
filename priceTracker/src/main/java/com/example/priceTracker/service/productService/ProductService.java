package com.example.priceTracker.service.productService;

import com.example.priceTracker.domain.product.Product;
import com.example.priceTracker.repository.productRepository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductsByUser(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

}
