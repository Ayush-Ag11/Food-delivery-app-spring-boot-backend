package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.Wallet;
import com.project.backend.foodelicious.entities.enums.TransactionMethod;
import com.project.backend.foodelicious.entities.enums.TransactionType;

import java.math.BigDecimal;

public interface WalletService {

    Wallet createNewWallet(User user);

    Wallet addMoneyToWallet(User user, BigDecimal amount, Order order,
                            TransactionMethod transactionMethod,
                            TransactionType transactionType);

    Wallet deductMoneyFromWallet(User user, BigDecimal amount, Order order,
                                 TransactionMethod transactionMethod,
                                 TransactionType transactionType);

    WalletDto getMyWallet(User user);
}