package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.DeliveryFeeStrategyManager;
import com.project.backend.foodelicious.strategies.OrderFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DefaultOrderFareCalculationStrategy implements OrderFareCalculationStrategy {

    private static final BigDecimal PLATFORM_FEE = new BigDecimal("10.00");
    private final DeliveryFeeStrategyManager deliveryFeeStrategyManager;

    @Override
    public BigDecimal calculateFare(OrderRequest orderRequest) {

        // Sum price * quantity for all items
        BigDecimal itemsTotal = orderRequest.getItems()
                .stream()
                .map(item -> item.getMenuItem().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Ask the manager which delivery fee strategy to use right now
        BigDecimal deliveryFee = deliveryFeeStrategyManager
                .getDeliveryFeeStrategy()
                .calculateDeliveryFee(orderRequest);

        return itemsTotal
                .add(deliveryFee)
                .add(PLATFORM_FEE);
    }
}