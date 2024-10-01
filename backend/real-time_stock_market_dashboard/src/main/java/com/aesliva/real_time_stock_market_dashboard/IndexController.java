package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/indexes")
    public List<Index> getAllIndexes() {
        return indexService.getAllIndexes();
    }

    @GetMapping("/sectors")
    public List<Index> getAllSectors() {
        return indexService.getAllSectors();
    }

    @GetMapping("/etf-data/{symbol}")
    public List<Map<String, Object>> getETFData(@PathVariable String symbol) {
        return indexService.getETFData(symbol);
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<?> getStockData(@PathVariable String symbol) {
        try {
            Map<String, Object> stockData = indexService.getDetailedStockData(symbol);
            return ResponseEntity.ok(stockData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found");
        }
    }
}