package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.Payment;
import com.project.backend.foodelicious.entities.enums.PaymentStatus;
import com.project.backend.foodelicious.strategies.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(Payment payment) {
        // Cash payment — no wallet deduction needed
        // Just mark payment as confirmed
        // In real world: delivery partner collects cash at door
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        return payment;
    }
}