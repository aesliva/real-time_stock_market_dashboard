package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexUpdateScheduler {

    @Autowired
    private IndexService indexService;

    @Scheduled(fixedRate = 3600000) // Update every hour
    public void updateIndexes() {
        indexService.updateIndexes();
    }

    @Scheduled(cron = "0 0 1 * * ?") // Run at 1:00 AM every day
    public void updateHistoricalData() {
        indexService.getAllIndexes().forEach(index -> indexService.updateHistoricalData(index.getSymbol()));
        indexService.getAllSectors().forEach(sector -> indexService.updateHistoricalData(sector.getSymbol()));
    }
}