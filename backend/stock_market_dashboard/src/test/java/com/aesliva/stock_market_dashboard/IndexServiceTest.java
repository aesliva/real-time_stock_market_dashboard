package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IndexServiceTest {

    @Autowired
    private IndexService indexService;

    @MockBean
    private IndexRepository indexRepository;

    @MockBean
    private AlphaVantageService alphaVantageService;

    @Test
    void testGetAllIndexes() {
        // Arrange
        Index index1 = new Index("SPY", new BigDecimal("400.00"), new BigDecimal("2.00"), new BigDecimal("0.5"));
        Index index2 = new Index("QQQ", new BigDecimal("300.00"), new BigDecimal("1.50"), new BigDecimal("0.5"));
        when(indexRepository.findBySymbol("SPY")).thenReturn(java.util.Optional.of(index1));
        when(indexRepository.findBySymbol("QQQ")).thenReturn(java.util.Optional.of(index2));

        // Act
        List<Index> result = indexService.getAllIndexes();

        // Assert
        assertEquals(2, result.size());
        assertEquals("SPY", result.get(0).getSymbol());
        assertEquals("QQQ", result.get(1).getSymbol());
    }

    @Test
    void testUpdateIndexes() {
        // Arrange
        Index updatedIndex = new Index("SPY", new BigDecimal("405.00"), new BigDecimal("5.00"), new BigDecimal("1.25"));
        when(alphaVantageService.fetchIndexData("SPY")).thenReturn(updatedIndex);
        when(indexRepository.findBySymbol("SPY")).thenReturn(java.util.Optional.of(new Index()));

        // Act
        indexService.updateIndexes();

        // Assert
        verify(indexRepository, times(1)).save(any(Index.class));
    }
}
