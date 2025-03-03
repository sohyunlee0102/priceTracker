package com.example.priceTracker.service.productService;

import com.example.priceTracker.domain.product.Product;
import com.example.priceTracker.dto.productDto.ProductResponseDTO;
import com.example.priceTracker.repository.productRepository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductsByUser(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public ProductResponseDTO.SaveDto saveProduct(Product product) {
        return new ProductResponseDTO.SaveDto(productRepository.save(product).getId(), LocalDateTime.now());
    }

}
