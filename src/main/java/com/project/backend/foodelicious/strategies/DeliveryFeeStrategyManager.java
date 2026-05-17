package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.strategies.impl.DistanceBasedDeliveryFeeStrategy;
import com.project.backend.foodelicious.strategies.impl.FlatRateDeliveryFeeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DeliveryFeeStrategyManager {

    private static final LocalTime PEAK_HOURS_START = LocalTime.of(12, 0);
    private static final LocalTime PEAK_HOURS_END = LocalTime.of(14, 0);
    private static final LocalTime DINNER_START = LocalTime.of(19, 0);
    private static final LocalTime DINNER_END = LocalTime.of(22, 0);
    private final FlatRateDeliveryFeeStrategy flatRateStrategy;
    private final DistanceBasedDeliveryFeeStrategy distanceBasedStrategy;

    public DeliveryFeeCalculationStrategy getDeliveryFeeStrategy() {
        LocalTime now = LocalTime.now();

        boolean isPeakHour =
                (now.isAfter(PEAK_HOURS_START) && now.isBefore(PEAK_HOURS_END)) ||
                        (now.isAfter(DINNER_START) && now.isBefore(DINNER_END));

        // Peak hours — distance based (higher fee for farther orders)
        // Off-peak — flat rate for everyone
        if (isPeakHour) {
            return distanceBasedStrategy;
        }
        return flatRateStrategy;
    }
}