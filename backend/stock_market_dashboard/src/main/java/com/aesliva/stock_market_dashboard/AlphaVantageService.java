package com.aesliva.stock_market_dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Service for interacting with the Alpha Vantage API.
 * 
 * This service handles all external API calls to Alpha Vantage for stock market
 * data.
 * It includes methods for fetching real-time quotes, historical data, and
 * company fundamentals.
 * 
 * @see <a href="https://www.alphavantage.co/documentation/">Alpha Vantage API
 *      Docs</a>
 */
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

    /**
     * Fetches current market data for a given index or ETF.
     * 
     * @param symbol The ticker symbol (e.g., "SPY", "QQQ")
     * @return Index object containing current price and daily changes
     * @throws RuntimeException if API call fails or response parsing fails
     */
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

    /**
     * Retrieves historical daily price data for an ETF.
     * Currently limited to the most recent 100 trading days.
     * 
     * @param symbol The ETF symbol to fetch data for
     * @return List of data points containing date and closing price
     * @throws RuntimeException if data fetching or parsing fails
     */
    public List<Map<String, Object>> fetchETFData(String symbol) {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode timeSeries = root.get("Time Series (Daily)");
            List<Map<String, Object>> dataPoints = new ArrayList<>();

            // Parse the time series data into a list of data points
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

    /**
     * Fetches comprehensive company data including fundamentals and current price.
     * 
     * This method combines data from multiple Alpha Vantage endpoints:
     * - Company Overview (fundamentals)
     * - Global Quote (current price)
     * 
     * Known Issues:
     * - Some fields may be null for new IPOs
     * - Market cap can be delayed
     * 
     * @param symbol Company ticker symbol
     * @return Map containing all available company data
     * @throws RuntimeException if data cannot be fetched or parsed
     */
    public Map<String, Object> fetchDetailedStockData(String symbol) {
        String url = String.format("%s?function=OVERVIEW&symbol=%s&apikey=%s", apiUrl, symbol, apiKey);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            Map<String, Object> stockData = new HashMap<>();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setMinimumFractionDigits(2);

            // TODO: Clean up, make more extensible

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

    /**
     * Helper method to fetch current price quote for a symbol.
     * Used internally by fetchDetailedStockData.
     */
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

    // Utility methods for formatting different types of numbers

    /**
     * Formats decimal numbers to 2 decimal places.
     * Returns original string if parsing fails.
     */
    private String formatDecimal(String value) {
        try {
            double number = Double.parseDouble(value);
            return String.format("%,.2f", number);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Formats percentage values with % symbol.
     * Handles decimal percentages (0.15 -> 15.00%).
     */
    private String formatPercentage(String value) {
        try {
            double number = Double.parseDouble(value);
            return String.format("%.2f%%", number * 100);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Formats large numbers into human-readable format with B/M suffix.
     * Example: 1500000000 -> $1.50B
     */
    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("$%.2fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("$%.2fM", number / 1_000_000.0);
        } else {
            return String.format("$%,d", number);
        }
    }

    // TODO: Add retry mechanism for failed API calls ?
    // TODO: Implement proper rate limiting
    // TODO: Add caching for frequently accessed data
}