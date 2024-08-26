package com.aesliva.real_time_stock_market_dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class AlphaVantageService {

    // TODO: Move to application.properties
    @Value("EL3L7DKLNK1TJIM8")
    private String apiKey;

    // TODO: Move to application.properties
    @Value("https://www.alphavantage.co/query")
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
                    globalQuote.get("01. symbol").asText(),
                    new BigDecimal(globalQuote.get("05. price").asText()),
                    new BigDecimal(globalQuote.get("09. change").asText()),
                    new BigDecimal(globalQuote.get("10. change percent").asText().replace("%", "")));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing AlphaVantage response", e);
        }
    }
}