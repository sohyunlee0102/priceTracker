package com.example.priceTracker.controller;

import com.example.priceTracker.domain.priceHistory.PriceHistory;
import com.example.priceTracker.service.priceHistoryService.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price-history")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PriceHistory>> getPriceHistoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(priceHistoryService.getPriceHistoryByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<PriceHistory> createPriceHistory(@RequestBody PriceHistory priceHistory) {
        return ResponseEntity.ok(priceHistoryService.savePriceHistory(priceHistory));
    }

}
