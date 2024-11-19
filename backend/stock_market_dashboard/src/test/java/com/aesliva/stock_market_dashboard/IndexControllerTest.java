package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexService indexService;

    // Main indexes
    private Index spyIndex;
    private Index qqqIndex;
    private Index vtiIndex;
    private Index iwmIndex;
    private Index vigIndex;
    private Index gldIndex;
    private Index aggIndex;

    // Sector indexes
    private Index xlfIndex;
    private Index xlkIndex;
    private Index xlvIndex;
    private Index xleIndex;
    private Index xlyIndex;
    private Index xlpIndex;
    private Index xliIndex;
    private Index xlbIndex;
    private Index xluIndex;
    private Index xlreIndex;

    @BeforeEach
    void setUp() {
        // Initialize main indexes
        spyIndex = new Index("SPY", new BigDecimal("400.00"), new BigDecimal("2.00"), new BigDecimal("0.5"));
        qqqIndex = new Index("QQQ", new BigDecimal("300.00"), new BigDecimal("1.50"), new BigDecimal("0.5"));
        vtiIndex = new Index("VTI", new BigDecimal("200.00"), new BigDecimal("1.00"), new BigDecimal("0.5"));
        iwmIndex = new Index("IWM", new BigDecimal("180.00"), new BigDecimal("1.20"), new BigDecimal("0.67"));
        vigIndex = new Index("VIG", new BigDecimal("150.00"), new BigDecimal("0.80"), new BigDecimal("0.53"));
        gldIndex = new Index("GLD", new BigDecimal("175.00"), new BigDecimal("-0.50"), new BigDecimal("-0.29"));
        aggIndex = new Index("AGG", new BigDecimal("100.00"), new BigDecimal("-0.20"), new BigDecimal("-0.20"));

        // Initialize sector indexes
        xlfIndex = new Index("XLF", new BigDecimal("35.00"), new BigDecimal("0.30"), new BigDecimal("0.86"));
        xlkIndex = new Index("XLK", new BigDecimal("150.00"), new BigDecimal("1.00"), new BigDecimal("0.67"));
        xlvIndex = new Index("XLV", new BigDecimal("120.00"), new BigDecimal("0.70"), new BigDecimal("0.58"));
        xleIndex = new Index("XLE", new BigDecimal("65.00"), new BigDecimal("0.40"), new BigDecimal("0.62"));
        xlyIndex = new Index("XLY", new BigDecimal("130.00"), new BigDecimal("0.60"), new BigDecimal("0.76"));
        xlpIndex = new Index("XLP", new BigDecimal("110.00"), new BigDecimal("0.40"), new BigDecimal("0.45"));
        xliIndex = new Index("XLI", new BigDecimal("140.00"), new BigDecimal("0.80"), new BigDecimal("0.92"));
        xlbIndex = new Index("XLB", new BigDecimal("160.00"), new BigDecimal("0.90"), new BigDecimal("1.09"));
        xluIndex = new Index("XLU", new BigDecimal("170.00"), new BigDecimal("1.10"), new BigDecimal("1.25"));
        xlreIndex = new Index("XLRE", new BigDecimal("180.00"), new BigDecimal("1.20"), new BigDecimal("1.41"));
    }

    @Test
    void getAllIndexes_ShouldReturnAllMainIndexes() throws Exception {
        List<Index> indexes = Arrays.asList(
                spyIndex, qqqIndex, vtiIndex, iwmIndex,
                vigIndex, gldIndex, aggIndex);
        when(indexService.getAllIndexes()).thenReturn(indexes);

        mockMvc.perform(get("/indexes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].symbol").value("SPY"))
                .andExpect(jsonPath("$[1].symbol").value("QQQ"))
                .andExpect(jsonPath("$[2].symbol").value("VTI"))
                .andExpect(jsonPath("$[3].symbol").value("IWM"))
                .andExpect(jsonPath("$[4].symbol").value("VIG"))
                .andExpect(jsonPath("$[5].symbol").value("GLD"))
                .andExpect(jsonPath("$[6].symbol").value("AGG"));
    }

    @Test
    void getAllSectors_ShouldReturnAllSectorIndexes() throws Exception {
        List<Index> sectors = Arrays.asList(
                xlfIndex, xlkIndex, xlvIndex, xleIndex, xlyIndex,
                xlpIndex, xliIndex, xlbIndex, xluIndex, xlreIndex);
        when(indexService.getAllSectors()).thenReturn(sectors);

        mockMvc.perform(get("/sectors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].symbol").value("XLF"))
                .andExpect(jsonPath("$[1].symbol").value("XLK"))
                .andExpect(jsonPath("$[2].symbol").value("XLV"))
                .andExpect(jsonPath("$[3].symbol").value("XLE"))
                .andExpect(jsonPath("$[4].symbol").value("XLY"))
                .andExpect(jsonPath("$[5].symbol").value("XLP"))
                .andExpect(jsonPath("$[6].symbol").value("XLI"))
                .andExpect(jsonPath("$[7].symbol").value("XLB"))
                .andExpect(jsonPath("$[8].symbol").value("XLU"))
                .andExpect(jsonPath("$[9].symbol").value("XLRE"));
    }

    @Test
    void getStockData_WithValidSymbol_ShouldReturnStockData() throws Exception {
        Map<String, Object> stockData = new HashMap<>();
        stockData.put("symbol", "AAPL");
        stockData.put("price", "150.00");
        when(indexService.getDetailedStockData("AAPL")).thenReturn(stockData);

        mockMvc.perform(get("/stock/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.symbol").value("AAPL"));
    }

    @Test
    void getStockData_WithInvalidSymbol_ShouldReturn404() throws Exception {
        when(indexService.getDetailedStockData(anyString()))
                .thenThrow(new RuntimeException("Stock not found"));

        mockMvc.perform(get("/stock/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Stock not found"));
    }
}