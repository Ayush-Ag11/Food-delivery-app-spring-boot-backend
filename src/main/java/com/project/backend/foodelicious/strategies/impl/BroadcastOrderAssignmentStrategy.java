package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.OrderAssignmentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BroadcastOrderAssignmentStrategy implements OrderAssignmentStrategy {

    @Override
    public void assignOrder(OrderRequest orderRequest, List<DeliveryPartner> matchedPartners) {

        if (matchedPartners.isEmpty()) {
            log.warn("No delivery partners available for order request id: {}",
                    orderRequest.getId());
            return;
        }

        // Notify all matched partners simultaneously
        // First one to call acceptOrder() gets it
        // In enhancement phase — replace log with actual push notification
        matchedPartners.forEach(partner ->
                log.info("Notifying delivery partner id: {} for order request id: {}",
                        partner.getId(), orderRequest.getId())
        );
    }
}