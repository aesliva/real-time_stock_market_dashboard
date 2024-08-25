package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/indexes")
    public List<Index> getAllIndexes() {
        return indexService.getAllIndexes();
    }
}