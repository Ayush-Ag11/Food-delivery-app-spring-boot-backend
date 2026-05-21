package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletDto {
    private Long id;
    private UserDto user;
    private BigDecimal balance;

    private List<WalletTransactionDto> transactions;
}