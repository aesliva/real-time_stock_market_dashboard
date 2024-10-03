package com.aesliva.stock_market_dashboard;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexRepository extends JpaRepository<Index, Long> {

    Optional<Index> findBySymbol(String symbol);
}
