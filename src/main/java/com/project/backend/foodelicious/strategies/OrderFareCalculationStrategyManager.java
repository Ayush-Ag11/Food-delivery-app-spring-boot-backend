package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.strategies.impl.DefaultOrderFareCalculationStrategy;
import com.project.backend.foodelicious.strategies.impl.SurgeOrderFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class OrderFareCalculationStrategyManager {

    private static final LocalTime PEAK_HOURS_START = LocalTime.of(12, 0);
    private static final LocalTime PEAK_HOURS_END = LocalTime.of(14, 0);
    private static final LocalTime DINNER_START = LocalTime.of(19, 0);
    private static final LocalTime DINNER_END = LocalTime.of(22, 0);
    private final DefaultOrderFareCalculationStrategy defaultStrategy;
    private final SurgeOrderFareCalculationStrategy surgeStrategy;

    public OrderFareCalculationStrategy getFareCalculationStrategy() {
        LocalTime now = LocalTime.now();

        boolean isPeakHour =
                (now.isAfter(PEAK_HOURS_START) && now.isBefore(PEAK_HOURS_END)) ||
                        (now.isAfter(DINNER_START) && now.isBefore(DINNER_END));

        if (isPeakHour) {
            return surgeStrategy;
        }

        return defaultStrategy;
    }
}