package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.OrderRequest;

import java.math.BigDecimal;

public interface OrderFareCalculationStrategy {

    BigDecimal calculateFare(OrderRequest orderRequest);
}