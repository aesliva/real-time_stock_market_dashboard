package com.aesliva.stock_market_dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RealTimeStockMarketDashboardApplicationTests {

	@Autowired
	private IndexController indexController;

	@Test
	void contextLoads() {
		assertNotNull(indexController);
	}
}