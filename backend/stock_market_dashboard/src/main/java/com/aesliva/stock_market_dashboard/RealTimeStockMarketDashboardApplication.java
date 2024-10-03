package com.aesliva.stock_market_dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealTimeStockMarketDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeStockMarketDashboardApplication.class, args);
	}

}
