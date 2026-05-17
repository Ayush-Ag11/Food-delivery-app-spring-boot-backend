package com.project.backend.foodelicious.strategies;

import com.project.backend.foodelicious.entities.enums.PaymentMethod;
import com.project.backend.foodelicious.strategies.impl.CashPaymentStrategy;
import com.project.backend.foodelicious.strategies.impl.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final CashPaymentStrategy cashPaymentStrategy;
    private final WalletPaymentStrategy walletPaymentStrategy;

    public PaymentStrategy getPaymentStrategy(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CASH -> cashPaymentStrategy;
            case WALLET -> walletPaymentStrategy;
        };
    }
}