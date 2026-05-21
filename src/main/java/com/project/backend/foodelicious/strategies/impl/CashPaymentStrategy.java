package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.Payment;
import com.project.backend.foodelicious.entities.enums.PaymentStatus;
import com.project.backend.foodelicious.repositories.PaymentRepository;
import com.project.backend.foodelicious.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {


    private final PaymentRepository paymentRepository;

    @Override
    public Payment processPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        payment.setPaymentTime(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}