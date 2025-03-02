package com.example.priceTracker.repository.priceHistoryRepository;

import com.example.priceTracker.domain.priceHistory.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByProductId(Long productId);
}
