package com.aesliva.stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 * Core service for managing market index and sector ETF data.
 * 
 * This service acts as the primary business logic layer, coordinating between
 * the database cache (IndexRepository) and external data provider
 * (AlphaVantageService).
 * It handles data fetching, caching, and updates for both broad market indexes
 * and sector ETFs.
 * 
 */
@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private AlphaVantageService alphaVantageService;

    private final List<String> indexSymbols = Arrays.asList("SPY", "QQQ", "VTI", "IWM", "VIG", "GLD", "AGG");

    // Mapping of sector ETF symbols to industry names.
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

    /**
     * Initializes the database with market data on application startup.
     * 
     * clearAllIndexes() call is commented out to prevent accidental
     * data deletion in production. Uncomment for testing or complete refresh.
     */
    @PostConstruct
    public void initializeDatabase() {
        // clearAllIndexes();
        updateIndexes();
    }

    /**
     * Retrieves current data for all tracked market indexes.
     * Uses cached data when available, fetches fresh data when needed.
     * 
     * @return List of Index objects containing current market data
     */
    public List<Index> getAllIndexes() {
        return indexSymbols.stream()
                .map(symbol -> indexRepository.findBySymbol(symbol)
                        .orElseGet(() -> {
                            Index newIndex = alphaVantageService.fetchIndexData(symbol);
                            return indexRepository.save(newIndex);
                        }))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves current data for all sector ETFs.
     * Similar to getAllIndexes() but includes sector names.
     * 
     * @return List of Index objects for sector ETFs
     */
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

    /**
     * Updates all tracked indexes and sectors with fresh market data.
     * This method is called periodically to keep the cache current.
     */
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

    /**
     * Clears all cached index data from the database.
     * Use with caution - this will trigger fresh API calls for all data.
     */
    public void clearAllIndexes() {
        indexRepository.deleteAll();
    }

    /**
     * Fetches historical price data for a specific ETF.
     * 
     * @param symbol The ETF symbol to fetch data for
     * @return List of daily price data points
     */
    public List<Map<String, Object>> getETFData(String symbol) {
        return alphaVantageService.fetchETFData(symbol);
    }

    /**
     * Retrieves basic price data for any stock symbol.
     * 
     * @param symbol The stock symbol to look up
     * @return Index object with current price and daily change
     */
    public Index getStockData(String symbol) {
        return alphaVantageService.fetchIndexData(symbol);
    }

    /**
     * Fetches detailed company information and current market data.
     * 
     * @param symbol The stock symbol to look up
     * @return Map containing comprehensive company and market data
     */
    public Map<String, Object> getDetailedStockData(String symbol) {
        return alphaVantageService.fetchDetailedStockData(symbol);
    }
}