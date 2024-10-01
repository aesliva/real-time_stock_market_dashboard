package com.aesliva.real_time_stock_market_dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AlphaVantageService {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Index fetchIndexData(String symbol) {
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode globalQuote = root.get("Global Quote");
            return new Index(
                    symbol,
                    new BigDecimal(globalQuote.get("05. price").asText()),
                    new BigDecimal(globalQuote.get("09. change").asText()),
                    new BigDecimal(globalQuote.get("10. change percent").asText().replace("%", "")));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing AlphaVantage response", e);
        }
    }

    public List<Map<String, Object>> fetchETFData(String symbol) {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode timeSeries = root.get("Time Series (Daily)");
            List<Map<String, Object>> dataPoints = new ArrayList<>();

            timeSeries.fields().forEachRemaining(entry -> {
                String date = entry.getKey();
                JsonNode values = entry.getValue();
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("date", date);
                dataPoint.put("close", Double.parseDouble(values.get("4. close").asText()));
                dataPoints.add(dataPoint);
            });

            return dataPoints;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing AlphaVantage response", e);
        }
    }

    public Map<String, Object> fetchDetailedStockData(String symbol) {
        String url = String.format("%s?function=OVERVIEW&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            Map<String, Object> stockData = new HashMap<>();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setMinimumFractionDigits(2);

            // Basic information
            stockData.put("symbol", root.get("Symbol").asText());
            stockData.put("name", root.get("Name").asText());
            stockData.put("description", root.get("Description").asText());
            stockData.put("exchange", root.get("Exchange").asText());
            stockData.put("currency", root.get("Currency").asText());
            stockData.put("country", root.get("Country").asText());
            stockData.put("sector", root.get("Sector").asText());
            stockData.put("industry", root.get("Industry").asText());

            // Financial metrics
            stockData.put("marketCap", formatLargeNumber(root.get("MarketCapitalization").asLong()));
            stockData.put("peRatio", formatDecimal(root.get("PERatio").asText()));
            stockData.put("pegRatio", formatDecimal(root.get("PEGRatio").asText()));
            stockData.put("bookValue", formatDecimal(root.get("BookValue").asText()));
            stockData.put("dividendPerShare", formatDecimal(root.get("DividendPerShare").asText()));
            stockData.put("dividendYield", root.get("DividendYield").asText());
            stockData.put("eps", formatDecimal(root.get("EPS").asText()));
            stockData.put("revenuePerShareTTM", formatDecimal(root.get("RevenuePerShareTTM").asText()));
            stockData.put("profitMargin", formatPercentage(root.get("ProfitMargin").asText()));
            stockData.put("operatingMarginTTM", formatPercentage(root.get("OperatingMarginTTM").asText()));
            stockData.put("returnOnAssetsTTM", formatPercentage(root.get("ReturnOnAssetsTTM").asText()));
            stockData.put("returnOnEquityTTM", formatPercentage(root.get("ReturnOnEquityTTM").asText()));
            stockData.put("52WeekHigh", formatDecimal(root.get("52WeekHigh").asText()));
            stockData.put("52WeekLow", formatDecimal(root.get("52WeekLow").asText()));

            // Add current price and change from the GLOBAL_QUOTE endpoint
            Map<String, Object> quoteData = fetchQuoteData(symbol);
            stockData.putAll(quoteData);

            return stockData;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing stock data", e);
        }
    }

    private Map<String, Object> fetchQuoteData(String symbol) {
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode globalQuote = root.get("Global Quote");
            Map<String, Object> quoteData = new HashMap<>();

            quoteData.put("price", formatDecimal(globalQuote.get("05. price").asText()));
            quoteData.put("change", formatDecimal(globalQuote.get("09. change").asText()));
            quoteData.put("changePercent", globalQuote.get("10. change percent").asText());

            return quoteData;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing quote data", e);
        }
    }

    private String formatDecimal(String value) {
        try {
            double number = Double.parseDouble(value);
            return String.format("%,.2f", number);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private String formatPercentage(String value) {
        try {
            double number = Double.parseDouble(value);
            return String.format("%.2f%%", number * 100);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("$%.2fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("$%.2fM", number / 1_000_000.0);
        } else {
            return String.format("$%,d", number);
        }
    }
}