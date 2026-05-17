package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.response.PaymentDto;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Payment;
import com.project.backend.foodelicious.entities.enums.PaymentStatus;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.PaymentRepository;
import com.project.backend.foodelicious.services.PaymentService;
import com.project.backend.foodelicious.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;
    private final ModelMapper modelMapper;

    @Override
    public void createNewPayment(Order order) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(order.getPaymentMethod())
                .amount(order.getTotalFare())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
    }

    @Override
    public PaymentDto processPayment(Order order) {
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for order with id: " + order.getId()
                ));

        // Pick strategy based on payment method and process
        Payment processedPayment = paymentStrategyManager
                .getPaymentStrategy(payment.getPaymentMethod())
                .processPayment(payment);

        return modelMapper.map(processedPayment, PaymentDto.class);
    }
}