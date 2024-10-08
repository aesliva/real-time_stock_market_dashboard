package com.aesliva.stock_market_dashboard;

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
}