package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.Payment;
import com.project.backend.foodelicious.entities.enums.PaymentStatus;
import com.project.backend.foodelicious.entities.enums.TransactionMethod;
import com.project.backend.foodelicious.entities.enums.TransactionType;
import com.project.backend.foodelicious.repositories.PaymentRepository;
import com.project.backend.foodelicious.services.WalletService;
import com.project.backend.foodelicious.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment processPayment(Payment payment) {

        // Who is paying and who is receiving
        var customer = payment.getOrder().getCustomer();
        var deliveryPartner = payment.getOrder().getDeliveryPartner();
        var amount = payment.getAmount();
        var order = payment.getOrder();

        // Deduct from customer wallet
        walletService.deductMoneyFromWallet(
                customer.getUser(),
                amount,
                order,
                TransactionMethod.WALLET,
                TransactionType.DEBIT
        );

        // Credit to delivery partner wallet
        walletService.addMoneyToWallet(
                deliveryPartner.getUser(),
                amount,
                order,
                TransactionMethod.WALLET,
                TransactionType.CREDIT
        );

        // Mark payment confirmed
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        return paymentRepository.save(payment);
    }
}