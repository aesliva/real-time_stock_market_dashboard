package com.aesliva.real_time_stock_market_dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;

    public List<Index> getAllIndexes() {
        return indexRepository.findAll();
    }
}