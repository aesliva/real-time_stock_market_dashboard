package com.aesliva.real_time_stock_market_dashboard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

// JPA entity for stock index
@Entity
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol;

    // Use BigDecimal for price, change, and changePercent to handle decimal values
    // accurately. Important for financial calculations.
    private BigDecimal price;
    private BigDecimal change;
    private BigDecimal changePercent;
}