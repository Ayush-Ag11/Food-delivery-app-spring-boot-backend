package com.project.backend.foodelicious.dtos.response;


import com.project.backend.foodelicious.entities.enums.OrderRequestStatus;
import com.project.backend.foodelicious.entities.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long id;
    private CustomerDto customer;
    private RestaurantDto restaurant;
    private List<OrderRequestItemDto> items;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private OrderRequestStatus orderRequestStatus;
    private BigDecimal totalFare;
    private LocalDateTime createdAt;
}