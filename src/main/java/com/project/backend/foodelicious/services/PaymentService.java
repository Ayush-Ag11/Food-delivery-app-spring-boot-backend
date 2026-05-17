package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.response.PaymentDto;
import com.project.backend.foodelicious.entities.Order;

public interface PaymentService {

    // Creates a pending payment record when order starts
    void createNewPayment(Order order);

    // Processes payment when order is delivered
    PaymentDto processPayment(Order order);
}