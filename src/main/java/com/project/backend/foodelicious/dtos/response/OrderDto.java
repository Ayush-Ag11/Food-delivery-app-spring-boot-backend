package com.project.backend.foodelicious.dtos.response;

import com.project.backend.foodelicious.entities.enums.OrderStatus;
import com.project.backend.foodelicious.entities.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private CustomerDto customer;
    private DeliveryPartnerDto deliveryPartner;
    private RestaurantDto restaurant;
    private List<OrderItemDto> items;
    private PaymentMethod paymentMethod;
    private OrderStatus orderStatus;
    private BigDecimal totalFare;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime deliveredAt;
    // otp intentionally excluded — security risk
}