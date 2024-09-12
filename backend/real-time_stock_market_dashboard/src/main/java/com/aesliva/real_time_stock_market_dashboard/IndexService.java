package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private AlphaVantageService alphaVantageService;

    private final List<String> indexSymbols = Arrays.asList("SPY", "QQQ", "VTI", "IWM", "VIG", "GLD", "AGG");

    @PostConstruct
    public void initializeDatabase() {
        clearAllIndexes();
        updateIndexes();
    }

    public List<Index> getAllIndexes() {
        return indexRepository.findAll();
    }

    public void updateIndexes() {
        indexSymbols.forEach(symbol -> {
            Index updatedIndex = alphaVantageService.fetchIndexData(symbol);
            indexRepository.findBySymbol(symbol)
                    .ifPresentOrElse(
                            existingIndex -> {
                                existingIndex.setPrice(updatedIndex.getPrice());
                                existingIndex.setChange(updatedIndex.getChange());
                                existingIndex.setChangePercent(updatedIndex.getChangePercent());
                                indexRepository.save(existingIndex);
                            },
                            () -> indexRepository.save(updatedIndex));
        });
    }

    public void clearAllIndexes() {
        indexRepository.deleteAll();
    }

    public List<Map<String, Object>> getETFData(String symbol) {
        return alphaVantageService.fetchETFData(symbol);
    }
}