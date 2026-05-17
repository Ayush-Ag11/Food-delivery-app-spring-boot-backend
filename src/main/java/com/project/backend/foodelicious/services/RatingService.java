package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.response.CustomerDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.entities.Order;

public interface RatingService {

    // Creates empty rating record when order starts
    void createNewRating(Order order);

    // Customer rates delivery partner after delivery
    DeliveryPartnerDto rateDeliveryPartner(Long orderId, Integer rating);

    // Delivery partner rates customer after delivery
    CustomerDto rateCustomer(Long orderId, Integer rating);
}