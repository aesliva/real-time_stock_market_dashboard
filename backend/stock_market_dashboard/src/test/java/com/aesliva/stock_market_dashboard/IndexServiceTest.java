package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IndexServiceTest {

    @InjectMocks
    private IndexService indexService;

    @Mock
    private IndexRepository indexRepository;

    @Mock
    private AlphaVantageService alphaVantageService;

    @BeforeEach
    void setUp() {
        when(indexRepository.findBySymbol("SPY")).thenReturn(
                Optional.of(new Index("SPY", new BigDecimal("400.00"), new BigDecimal("2.00"), new BigDecimal("0.5"))));
        when(indexRepository.findBySymbol("QQQ")).thenReturn(
                Optional.of(new Index("QQQ", new BigDecimal("300.00"), new BigDecimal("1.50"), new BigDecimal("0.5"))));
    }

    @Test
    void testGetAllIndexes() {
        List<Index> result = indexService.getAllIndexes();

        assertEquals(2, result.size());
        assertEquals("SPY", result.get(0).getSymbol());
        assertEquals("QQQ", result.get(1).getSymbol());

        // Verify that findBySymbol was called for both symbols
        verify(indexRepository, times(1)).findBySymbol("SPY");
        verify(indexRepository, times(1)).findBySymbol("QQQ");
    }

    @Test
    void testUpdateIndexes() {
        // Arrange
        Index updatedIndex = new Index("SPY", new BigDecimal("405.00"), new BigDecimal("5.00"), new BigDecimal("1.25"));
        when(alphaVantageService.fetchIndexData("SPY")).thenReturn(updatedIndex);
        when(indexRepository.findBySymbol("SPY")).thenReturn(Optional.of(new Index()));

        // Act
        indexService.updateIndexes();

        // Assert
        verify(indexRepository, times(1)).save(any(Index.class));
    }
}
