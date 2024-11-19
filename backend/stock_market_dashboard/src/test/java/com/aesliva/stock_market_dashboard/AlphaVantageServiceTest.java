package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class AlphaVantageServiceTest {

    @InjectMocks
    private AlphaVantageService alphaVantageService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    void fetchIndexData_ShouldReturnIndexObject() {
        Map<String, Object> mockResponse = Map.of(
                "Global Quote", Map.of(
                        "01. symbol", "SPY",
                        "05. price", "400.00",
                        "09. change", "2.00",
                        "10. change percent", "0.5%"));

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(mockResponse);

        Index result = alphaVantageService.fetchIndexData("SPY");

        assertNotNull(result);
        assertEquals("SPY", result.getSymbol());
        assertEquals(new BigDecimal("400.00"), result.getPrice());
        assertEquals(new BigDecimal("2.00"), result.getChange());
    }

    @Test
    void fetchDetailedStockData_ShouldReturnStockData() {
        Map<String, Object> mockResponse = Map.of(
                "Global Quote", Map.of(
                        "01. symbol", "AAPL",
                        "05. price", "150.00",
                        "09. change", "2.50",
                        "10. change percent", "1.67%"));

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = alphaVantageService.fetchDetailedStockData("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.get("symbol"));
        assertTrue(result.containsKey("price"));
    }

    @Test
    void fetchIndexData_WhenAPIFails_ShouldThrowException() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("API Error"));

        assertThrows(RuntimeException.class, () -> {
            alphaVantageService.fetchIndexData("SPY");
        });
    }
}