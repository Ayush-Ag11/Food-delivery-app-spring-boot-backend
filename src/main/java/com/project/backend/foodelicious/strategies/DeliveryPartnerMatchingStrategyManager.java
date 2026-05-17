package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.strategies.impl.HighestRatedDeliveryPartnerMatchingStrategy;
import com.project.backend.foodelicious.strategies.impl.NearestDeliveryPartnerMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DeliveryPartnerMatchingStrategyManager {

    // Peak hours — use highest rated strategy to ensure quality
    // Off-peak hours — use nearest strategy for speed
    private static final LocalTime PEAK_HOURS_START = LocalTime.of(12, 0); // 12:00 PM
    private static final LocalTime PEAK_HOURS_END = LocalTime.of(14, 0);   // 2:00 PM
    private static final LocalTime DINNER_START = LocalTime.of(19, 0);     // 7:00 PM
    private static final LocalTime DINNER_END = LocalTime.of(22, 0);       // 10:00 PM
    private final NearestDeliveryPartnerMatchingStrategy nearestStrategy;
    private final HighestRatedDeliveryPartnerMatchingStrategy highestRatedStrategy;

    public DeliveryPartnerMatchingStrategy getMatchingStrategy() {
        LocalTime now = LocalTime.now();

        boolean isPeakHour =
                (now.isAfter(PEAK_HOURS_START) && now.isBefore(PEAK_HOURS_END)) ||
                        (now.isAfter(DINNER_START) && now.isBefore(DINNER_END));

        if (isPeakHour) {
            // During peak hours prioritize highest rated partners
            return highestRatedStrategy;
        }

        // Off-peak — just find the nearest available partner
        return nearestStrategy;
    }
}