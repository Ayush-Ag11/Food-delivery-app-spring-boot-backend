package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.OrderRequest;

import java.math.BigDecimal;

public interface DeliveryFeeCalculationStrategy {

    BigDecimal calculateDeliveryFee(OrderRequest orderRequest);
}