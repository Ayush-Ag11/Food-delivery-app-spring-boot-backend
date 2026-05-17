package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.OrderAssignmentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SequentialOrderAssignmentStrategy implements OrderAssignmentStrategy {

    // How many top partners to notify (notify top 3 nearest/highest rated)
    private static final int TOP_PARTNERS_TO_NOTIFY = 3;

    @Override
    public void assignOrder(OrderRequest orderRequest, List<DeliveryPartner> matchedPartners) {

        if (matchedPartners.isEmpty()) {
            log.warn("No delivery partners available for order request id: {}",
                    orderRequest.getId());
            return;
        }

        // Notify only top N partners sequentially
        // In enhancement phase — add timeout and fallback to next partner
        matchedPartners.stream()
                .limit(TOP_PARTNERS_TO_NOTIFY)
                .forEach(partner ->
                        log.info("Sequentially notifying partner id: {} for order request id: {}",
                                partner.getId(), orderRequest.getId())
                );
    }
}