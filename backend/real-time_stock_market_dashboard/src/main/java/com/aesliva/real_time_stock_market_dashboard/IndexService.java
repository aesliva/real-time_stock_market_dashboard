package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private AlphaVantageService alphaVantageService;

    private final List<String> indexSymbols = Arrays.asList("SPY", "QQQ", "VTI", "IWM", "VIG", "GLD", "AGG");
    private final Map<String, String> sectorSymbols = Map.of(
            "XLF", "Financials",
            "XLK", "Technology",
            "XLV", "Healthcare",
            "XLE", "Energy",
            "XLY", "Consumer Discretionary",
            "XLP", "Consumer Staples",
            "XLI", "Industrials",
            "XLB", "Materials",
            "XLU", "Utilities",
            "XLRE", "Real Estate");

    @PostConstruct
    public void initializeDatabase() {
        clearAllIndexes();
        updateIndexes();
        for (String symbol : indexSymbols) {
            updateHistoricalData(symbol);
        }
        for (String symbol : sectorSymbols.keySet()) {
            updateHistoricalData(symbol);
        }
    }

    public List<Index> getAllIndexes() {
        return indexSymbols.stream()
                .map(symbol -> indexRepository.findBySymbol(symbol)
                        .orElseGet(() -> {
                            Index newIndex = alphaVantageService.fetchIndexData(symbol);
                            return indexRepository.save(newIndex);
                        }))
                .collect(Collectors.toList());
    }

    public List<Index> getAllSectors() {
        return sectorSymbols.keySet().stream()
                .map(symbol -> indexRepository.findBySymbol(symbol)
                        .orElseGet(() -> {
                            Index newIndex = alphaVantageService.fetchIndexData(symbol);
                            newIndex.setName(sectorSymbols.get(symbol));
                            return indexRepository.save(newIndex);
                        }))
                .collect(Collectors.toList());
    }

    public void updateIndexes() {
        Stream.concat(indexSymbols.stream(), sectorSymbols.keySet().stream())
                .forEach(symbol -> {
                    Index updatedIndex = alphaVantageService.fetchIndexData(symbol);
                    indexRepository.findBySymbol(symbol)
                            .ifPresentOrElse(
                                    existingIndex -> {
                                        existingIndex.setPrice(updatedIndex.getPrice());
                                        existingIndex.setChange(updatedIndex.getChange());
                                        existingIndex.setChangePercent(updatedIndex.getChangePercent());
                                        indexRepository.save(existingIndex);
                                    },
                                    () -> {
                                        if (sectorSymbols.containsKey(symbol)) {
                                            updatedIndex.setName(sectorSymbols.get(symbol));
                                        }
                                        indexRepository.save(updatedIndex);
                                    });
                });
    }

    public void clearAllIndexes() {
        indexRepository.deleteAll();
    }

    public List<Map<String, Object>> getETFData(String symbol) {
        return alphaVantageService.fetchETFData(symbol);
    }

    public Index getStockData(String symbol) {
        return alphaVantageService.fetchIndexData(symbol);
    }

    public Map<String, Object> getDetailedStockData(String symbol) {
        return alphaVantageService.fetchDetailedStockData(symbol);
    }

    public void updateHistoricalData(String symbol) {
        Index index = indexRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Index not found"));
        List<HistoricalData> historicalData = alphaVantageService.fetchHistoricalData(symbol, index);
        index.setHistoricalData(historicalData);
        indexRepository.save(index);
    }

    public List<HistoricalData> getHistoricalData(String symbol, String range) {
        Index index = indexRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Index not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (range) {
            case "1y":
                startDate = endDate.minusYears(1);
                break;
            case "5y":
                startDate = endDate.minusYears(5);
                break;
            case "10y":
                startDate = endDate.minusYears(10);
                break;
            default:
                throw new IllegalArgumentException("Invalid range");
        }

        return index.getHistoricalData().stream()
                .filter(data -> !data.getDate().isBefore(startDate) && !data.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(HistoricalData::getDate))
                .collect(Collectors.toList());
    }
}