package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private AlphaVantageService alphaVantageService;

    private final List<String> indexSymbols = Arrays.asList("IBM");

    public List<Index> getAllIndexes() {
        return indexRepository.findAll();
    }

    public void updateIndexes() {
        List<Index> updatedIndexes = indexSymbols.stream()
                .map(alphaVantageService::fetchIndexData)
                .collect(Collectors.toList());
        indexRepository.saveAll(updatedIndexes);
    }
}