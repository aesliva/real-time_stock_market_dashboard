package com.aesliva.real_time_stock_market_dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
}