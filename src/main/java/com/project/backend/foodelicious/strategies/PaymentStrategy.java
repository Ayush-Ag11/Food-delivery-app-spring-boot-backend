package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.Payment;

public interface PaymentStrategy {

    // Processes payment and returns updated Payment entity
    Payment processPayment(Payment payment);
}