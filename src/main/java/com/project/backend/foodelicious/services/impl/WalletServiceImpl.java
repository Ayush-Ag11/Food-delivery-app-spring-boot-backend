package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.Wallet;
import com.project.backend.foodelicious.entities.WalletTransaction;
import com.project.backend.foodelicious.entities.enums.TransactionMethod;
import com.project.backend.foodelicious.entities.enums.TransactionType;
import com.project.backend.foodelicious.exceptions.InsufficientBalanceException;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.WalletRepository;
import com.project.backend.foodelicious.repositories.WalletTransactionRepository;
import com.project.backend.foodelicious.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public Wallet createNewWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, BigDecimal amount, Order order,
                                   TransactionMethod transactionMethod,
                                   TransactionType transactionType) {

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user with id: " + user.getId()
                ));

        // Add amount to balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // Record the transaction
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .order(order)
                .amount(amount)
                .transactionType(transactionType)
                .transactionMethod(transactionMethod)
                .transactionId(UUID.randomUUID().toString())
                .build();

        walletTransactionRepository.save(transaction);

        return wallet;
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, BigDecimal amount, Order order,
                                        TransactionMethod transactionMethod,
                                        TransactionType transactionType) {

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user with id: " + user.getId()
                ));

        // Check sufficient balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in wallet. Available: "
                            + wallet.getBalance() + ", Required: " + amount
            );
        }

        // Deduct amount from balance
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Record the transaction
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .order(order)
                .amount(amount)
                .transactionType(transactionType)
                .transactionMethod(transactionMethod)
                .transactionId(UUID.randomUUID().toString())
                .build();

        walletTransactionRepository.save(transaction);

        return wallet;
    }

    @Override
    public WalletDto getMyWallet(User user) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user with id: " + user.getId()
                ));
        return modelMapper.map(wallet, WalletDto.class);
    }
}