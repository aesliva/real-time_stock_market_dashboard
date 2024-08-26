package com.aesliva.real_time_stock_market_dashboard;

import jakarta.persistence.*;
import java.math.BigDecimal;

// JPA entity for stock index
@Entity
@Table(name = "stock_index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String symbol;

    // Use BigDecimal for price, change, and changePercent to handle decimal values
    // accurately. Important for financial calculations.
    private BigDecimal price;
    @Column(name = "price_change")
    private BigDecimal change;
    @Column(name = "price_change_percent")
    private BigDecimal changePercent;

    public Index() {
    }

    public Index(String name, String symbol, BigDecimal price, BigDecimal change, BigDecimal changePercent) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }
}