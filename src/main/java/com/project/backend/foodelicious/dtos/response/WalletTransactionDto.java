package com.project.backend.foodelicious.dtos.response;

import com.project.backend.foodelicious.entities.enums.TransactionMethod;
import com.project.backend.foodelicious.entities.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransactionDto {
    private Long id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionMethod transactionMethod;
    private String transactionId;
    private LocalDateTime timestamp;
}