package com.aesliva.stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller handling stock market index and sector data requests.
 * 
 * This controller provides endpoints for retrieving broad market indexes (like
 * SPY, QQQ),
 * sector-specific ETFs (like XLF, XLK), and individual stock data. All
 * endpoints support
 * CORS for GitHub Pages frontend and AWS API Gateway.
 * 
 * Note: We're rate-limited by Alpha Vantage's API, so we cache responses where
 * possible.
 */
@RestController
@CrossOrigin(origins = { "https://aesliva.github.io", "https://0tnr4jx1b5.execute-api.us-west-1.amazonaws.com/prod" })
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * Retrieves current data for all tracked market indexes (SPY, QQQ, etc.).
     * Data is cached and updated every hour to avoid hitting API limits.
     * 
     * @return List of Index objects containing current price and daily changes
     */
    @GetMapping("/indexes")
    public List<Index> getAllIndexes() {
        return indexService.getAllIndexes();
    }

    /**
     * Retrieves current data for all sector ETFs.
     * 
     * @return List of Index objects for sector ETFs
     */
    @GetMapping("/sectors")
    public List<Index> getAllSectors() {
        return indexService.getAllSectors();
    }

    /**
     * Fetches historical price data for a specific ETF.
     * 
     * @param symbol The ETF symbol (e.g., "SPY", "XLF")
     * @return List of daily price data points
     */
    @GetMapping("/etf-data/{symbol}")
    public List<Map<String, Object>> getETFData(@PathVariable String symbol) {
        return indexService.getETFData(symbol);
    }

    /**
     * Retrieves detailed data for an individual stock.
     * Includes fundamentals, price data, and company info.
     * 
     * @param symbol The stock symbol (e.g., "AAPL")
     * @return ResponseEntity containing either stock data or error message
     */
    @GetMapping("/stock/{symbol}")
    public ResponseEntity<?> getStockData(@PathVariable String symbol) {
        try {
            Map<String, Object> stockData = indexService.getDetailedStockData(symbol);
            return ResponseEntity.ok(stockData);
        } catch (Exception e) {
            // Log the actual error but return a generic message to client
            // TODO: Add proper error logging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found");
        }
    }
}