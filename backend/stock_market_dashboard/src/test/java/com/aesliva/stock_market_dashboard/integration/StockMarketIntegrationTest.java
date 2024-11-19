package com.aesliva.stock_market_dashboard.integration;

import com.aesliva.stock_market_dashboard.Index;
import com.aesliva.stock_market_dashboard.IndexRepository;
import com.aesliva.stock_market_dashboard.AlphaVantageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class StockMarketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IndexRepository indexRepository;

    @MockBean
    private AlphaVantageService alphaVantageService;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        indexRepository.deleteAll();

        // Setup mock responses for AlphaVantageService
        Index spyIndex = new Index("SPY", new BigDecimal("400.00"), new BigDecimal("2.00"), new BigDecimal("0.5"));
        when(alphaVantageService.fetchIndexData("SPY")).thenReturn(spyIndex);

        Map<String, Object> stockData = new HashMap<>();
        stockData.put("symbol", "AAPL");
        stockData.put("price", "150.00");
        stockData.put("name", "Apple Inc.");
        stockData.put("marketCap", "$2.5T");
        when(alphaVantageService.fetchDetailedStockData("AAPL")).thenReturn(stockData);
    }

    @Test
    void testCompleteIndexFlow() throws Exception {
        // First request should trigger data fetch and storage
        mockMvc.perform(get("/indexes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Verify data was stored in database
        List<Index> storedIndexes = indexRepository.findAll();
        assertFalse(storedIndexes.isEmpty());

        // Second request should use stored data
        mockMvc.perform(get("/indexes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.symbol=='SPY')].price").value("400.00"));
    }

    @Test
    void testStockDataFlow() throws Exception {
        // Test detailed stock data endpoint
        mockMvc.perform(get("/stock/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc."))
                .andExpect(jsonPath("$.marketCap").value("$2.5T"));
    }

    @Test
    void testErrorHandling() throws Exception {
        // Test invalid stock symbol
        when(alphaVantageService.fetchDetailedStockData("INVALID"))
                .thenThrow(new RuntimeException("Stock not found"));

        mockMvc.perform(get("/stock/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Stock not found"));
    }

    @Test
    void testConcurrentRequests() throws Exception {
        // Simulate concurrent requests
        Thread thread1 = new Thread(() -> {
            try {
                mockMvc.perform(get("/indexes"))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                fail("Concurrent request failed: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                mockMvc.perform(get("/stock/AAPL"))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                fail("Concurrent request failed: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    @Test
    void testDatabasePersistence() throws Exception {
        // Save test data
        Index testIndex = new Index("TEST", new BigDecimal("100.00"), new BigDecimal("1.00"), new BigDecimal("1.0"));
        indexRepository.save(testIndex);

        // Verify data persistence
        Optional<Index> retrieved = indexRepository.findBySymbol("TEST");
        assertTrue(retrieved.isPresent());
        assertEquals("TEST", retrieved.get().getSymbol());
        assertEquals(new BigDecimal("100.00"), retrieved.get().getPrice());
    }

    @Test
    void testCORSConfiguration() throws Exception {
        mockMvc.perform(get("/indexes")
                .header("Origin", "https://aesliva.github.io"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://aesliva.github.io"));
    }
}