package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.DeliveryFeeCalculationStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FlatRateDeliveryFeeStrategy implements DeliveryFeeCalculationStrategy {

    private static final BigDecimal FLAT_DELIVERY_FEE = new BigDecimal("30.00");

    @Override
    public BigDecimal calculateDeliveryFee(OrderRequest orderRequest) {
        return FLAT_DELIVERY_FEE;
    }
}