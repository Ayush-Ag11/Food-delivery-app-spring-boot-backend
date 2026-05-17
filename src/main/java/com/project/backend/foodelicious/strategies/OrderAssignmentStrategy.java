package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;

import java.util.List;

public interface OrderAssignmentStrategy {

    // Decides how matched partners are notified/assigned
    void assignOrder(OrderRequest orderRequest, List<DeliveryPartner> matchedPartners);
}