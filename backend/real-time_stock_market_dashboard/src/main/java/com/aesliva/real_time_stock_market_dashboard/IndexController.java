package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/indexes")
    public List<Index> getAllIndexes() {
        return indexService.getAllIndexes();
    }
}