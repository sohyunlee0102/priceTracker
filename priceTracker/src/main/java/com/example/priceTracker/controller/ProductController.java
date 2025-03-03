package com.example.priceTracker.controller;

import com.example.priceTracker.apiPayload.ApiResponse;
import com.example.priceTracker.domain.product.Product;
import com.example.priceTracker.domain.user.User;
import com.example.priceTracker.dto.productDto.ProductRequestDTO;
import com.example.priceTracker.dto.productDto.ProductResponseDTO;
import com.example.priceTracker.service.productService.ProductCrawlerService;
import com.example.priceTracker.service.productService.ProductService;
import com.example.priceTracker.service.userService.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductCrawlerService productCrawlerService;
    private final UserService userService;

    @GetMapping("/search")
    public ApiResponse<List<ProductResponseDTO.ProductDto>> searchProducts
            (@RequestParam String keyword) throws IOException {
        return ApiResponse.onSuccess(productCrawlerService.crawlProduct(keyword));
    }

    @PostMapping("/save")
    public ApiResponse<ProductResponseDTO.SaveDto> saveProduct(@Valid @RequestBody ProductRequestDTO.productDto request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUserByEmail(userDetails.getUsername());
        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .url(request.getUrl())
                .platform(request.getPlatform())
                .price(request.getPrice())
                .user(user)
                .build();

        return ApiResponse.onSuccess(productService.saveProduct(product));
    }

}
