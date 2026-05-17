package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.OrderFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class SurgeOrderFareCalculationStrategy implements OrderFareCalculationStrategy {

    // During peak hours — 1.5x surge multiplier
    private static final BigDecimal SURGE_MULTIPLIER = new BigDecimal("1.5");
    private final DefaultOrderFareCalculationStrategy defaultStrategy;

    @Override
    public BigDecimal calculateFare(OrderRequest orderRequest) {

        // Start with the default fare
        BigDecimal defaultFare = defaultStrategy.calculateFare(orderRequest);

        // Apply surge multiplier and round to 2 decimal places
        return defaultFare
                .multiply(SURGE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
}