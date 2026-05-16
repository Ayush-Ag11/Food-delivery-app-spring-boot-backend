package com.project.backend.foodelicious.dtos.response;

import com.project.backend.foodelicious.entities.enums.PaymentMethod;
import com.project.backend.foodelicious.entities.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private Long orderId;           // just the ID, not full OrderDto — avoids deep nesting
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentTime;
}