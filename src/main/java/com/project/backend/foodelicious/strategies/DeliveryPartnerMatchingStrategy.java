package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;

import java.util.List;

public interface DeliveryPartnerMatchingStrategy {

    // Returns a list of matched delivery partners for a given order request
    // List because we notify multiple partners — first one to accept gets the order
    List<DeliveryPartner> findMatchingDeliveryPartner(OrderRequest orderRequest);
}