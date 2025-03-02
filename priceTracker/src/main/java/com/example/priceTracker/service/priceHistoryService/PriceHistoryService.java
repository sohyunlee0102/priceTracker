package com.example.priceTracker.service.priceHistoryService;

import com.example.priceTracker.domain.priceHistory.PriceHistory;
import com.example.priceTracker.repository.priceHistoryRepository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    public List<PriceHistory> getPriceHistoryByProduct(Long productId) {
        return priceHistoryRepository.findByProductId(productId);
    }

    public PriceHistory savePriceHistory(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

}
