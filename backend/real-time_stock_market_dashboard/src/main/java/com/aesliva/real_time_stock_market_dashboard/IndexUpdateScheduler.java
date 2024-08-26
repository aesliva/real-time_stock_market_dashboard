package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexUpdateScheduler {

    @Autowired
    private IndexService indexService;

    @Scheduled(fixedRate = 300000) // Update every 5 minutes
    public void updateIndexes() {
        indexService.updateIndexes();
    }
}