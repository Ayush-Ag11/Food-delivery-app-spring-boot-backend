package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.strategies.impl.BroadcastOrderAssignmentStrategy;
import com.project.backend.foodelicious.strategies.impl.SequentialOrderAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class OrderAssignmentStrategyManager {

    private static final LocalTime PEAK_HOURS_START = LocalTime.of(12, 0);
    private static final LocalTime PEAK_HOURS_END = LocalTime.of(14, 0);
    private static final LocalTime DINNER_START = LocalTime.of(19, 0);
    private static final LocalTime DINNER_END = LocalTime.of(22, 0);
    private final BroadcastOrderAssignmentStrategy broadcastStrategy;
    private final SequentialOrderAssignmentStrategy sequentialStrategy;

    public OrderAssignmentStrategy getAssignmentStrategy() {
        LocalTime now = LocalTime.now();

        boolean isPeakHour =
                (now.isAfter(PEAK_HOURS_START) && now.isBefore(PEAK_HOURS_END)) ||
                        (now.isAfter(DINNER_START) && now.isBefore(DINNER_END));

        // Peak hours — broadcast to all so order gets picked up fast
        // Off-peak — sequential to avoid spamming all partners
        if (isPeakHour) {
            return broadcastStrategy;
        }
        return sequentialStrategy;
    }
}