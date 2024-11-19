package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class IndexServiceTest {

    @InjectMocks
    private IndexService indexService;

    @Mock
    private IndexRepository indexRepository;

    @Mock
    private AlphaVantageService alphaVantageService;

    private Index spyIndex;

    @BeforeEach
    void setUp() {
        spyIndex = new Index("SPY", new BigDecimal("400.00"), new BigDecimal("2.00"), new BigDecimal("0.5"));
        Index qqqIndex = new Index("QQQ", new BigDecimal("300.00"), new BigDecimal("1.50"), new BigDecimal("0.5"));
        Index vtiIndex = new Index("VTI", new BigDecimal("200.00"), new BigDecimal("1.00"), new BigDecimal("0.5"));
        Index iwmIndex = new Index("IWM", new BigDecimal("180.00"), new BigDecimal("1.20"), new BigDecimal("0.67"));
        Index vigIndex = new Index("VIG", new BigDecimal("150.00"), new BigDecimal("0.80"), new BigDecimal("0.53"));
        Index gldIndex = new Index("GLD", new BigDecimal("175.00"), new BigDecimal("-0.50"), new BigDecimal("-0.29"));
        Index aggIndex = new Index("AGG", new BigDecimal("100.00"), new BigDecimal("-0.20"), new BigDecimal("-0.20"));

        // Setup sector indexes
        Index xlfIndex = new Index("XLF", new BigDecimal("35.00"), new BigDecimal("0.30"), new BigDecimal("0.86"));
        Index xlkIndex = new Index("XLK", new BigDecimal("150.00"), new BigDecimal("1.00"), new BigDecimal("0.67"));
        Index xlvIndex = new Index("XLV", new BigDecimal("120.00"), new BigDecimal("0.70"), new BigDecimal("0.58"));
        Index xlyIndex = new Index("XLY", new BigDecimal("130.00"), new BigDecimal("0.60"), new BigDecimal("0.76"));
        Index xlpIndex = new Index("XLP", new BigDecimal("110.00"), new BigDecimal("0.40"), new BigDecimal("0.45"));
        Index xliIndex = new Index("XLI", new BigDecimal("140.00"), new BigDecimal("0.80"), new BigDecimal("0.92"));
        Index xlbIndex = new Index("XLB", new BigDecimal("160.00"), new BigDecimal("0.90"), new BigDecimal("1.09"));
        Index xluIndex = new Index("XLU", new BigDecimal("170.00"), new BigDecimal("1.10"), new BigDecimal("1.25"));
        Index xlreIndex = new Index("XLRE", new BigDecimal("180.00"), new BigDecimal("1.20"), new BigDecimal("1.41"));

        when(indexRepository.findBySymbol("SPY")).thenReturn(Optional.of(spyIndex));
        when(indexRepository.findBySymbol("QQQ")).thenReturn(Optional.of(qqqIndex));
        when(indexRepository.findBySymbol("VTI")).thenReturn(Optional.of(vtiIndex));
        when(indexRepository.findBySymbol("IWM")).thenReturn(Optional.of(iwmIndex));
        when(indexRepository.findBySymbol("VIG")).thenReturn(Optional.of(vigIndex));
        when(indexRepository.findBySymbol("GLD")).thenReturn(Optional.of(gldIndex));
        when(indexRepository.findBySymbol("AGG")).thenReturn(Optional.of(aggIndex));

        when(indexRepository.findBySymbol("XLF")).thenReturn(Optional.of(xlfIndex));
        when(indexRepository.findBySymbol("XLK")).thenReturn(Optional.of(xlkIndex));
        when(indexRepository.findBySymbol("XLV")).thenReturn(Optional.of(xlvIndex));
        when(indexRepository.findBySymbol("XLY")).thenReturn(Optional.of(xlyIndex));
        when(indexRepository.findBySymbol("XLP")).thenReturn(Optional.of(xlpIndex));
        when(indexRepository.findBySymbol("XLI")).thenReturn(Optional.of(xliIndex));
        when(indexRepository.findBySymbol("XLB")).thenReturn(Optional.of(xlbIndex));
        when(indexRepository.findBySymbol("XLU")).thenReturn(Optional.of(xluIndex));
        when(indexRepository.findBySymbol("XLRE")).thenReturn(Optional.of(xlreIndex));
    }

    @Test
    void getAllIndexes_ShouldReturnAllConfiguredIndexes() {
        List<Index> result = indexService.getAllIndexes();

        assertNotNull(result);
        assertEquals(7, result.size()); // Should be 7 main indexes
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("SPY")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("QQQ")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("VTI")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("IWM")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("VIG")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("GLD")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("AGG")));
    }

    @Test
    void getAllIndexes_WhenIndexNotFound_ShouldFetchFromAlphaVantage() {
        when(indexRepository.findBySymbol("SPY")).thenReturn(Optional.empty());
        when(alphaVantageService.fetchIndexData("SPY")).thenReturn(spyIndex);
        when(indexRepository.save(any(Index.class))).thenReturn(spyIndex);

        List<Index> result = indexService.getAllIndexes();

        verify(alphaVantageService).fetchIndexData("SPY");
        verify(indexRepository).save(any(Index.class));
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllSectors_ShouldReturnAllConfiguredSectors() {
        List<Index> result = indexService.getAllSectors();

        assertNotNull(result);
        assertEquals(10, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLF")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLK")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLV")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLE")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLY")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLP")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLI")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLB")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLU")));
        assertTrue(result.stream().anyMatch(i -> i.getSymbol().equals("XLRE")));
    }

    @Test
    void updateIndexes_ShouldUpdateAllConfiguredIndexes() {
        when(alphaVantageService.fetchIndexData(anyString())).thenReturn(spyIndex);
        when(indexRepository.save(any(Index.class))).thenReturn(spyIndex);

        indexService.updateIndexes();

        verify(alphaVantageService, atLeastOnce()).fetchIndexData(anyString());
        verify(indexRepository, atLeastOnce()).save(any(Index.class));
    }

    @Test
    void getDetailedStockData_ShouldReturnStockInformation() {
        Map<String, Object> mockStockData = Map.of(
                "symbol", "AAPL",
                "price", "150.00",
                "change", "2.50");
        when(alphaVantageService.fetchDetailedStockData("AAPL")).thenReturn(mockStockData);

        Map<String, Object> result = indexService.getDetailedStockData("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.get("symbol"));
        verify(alphaVantageService).fetchDetailedStockData("AAPL");
    }

    @Test
    void getDetailedStockData_WhenStockNotFound_ShouldThrowException() {
        when(alphaVantageService.fetchDetailedStockData("INVALID"))
                .thenThrow(new RuntimeException("Stock not found"));

        assertThrows(RuntimeException.class, () -> {
            indexService.getDetailedStockData("INVALID");
        });
    }
}
