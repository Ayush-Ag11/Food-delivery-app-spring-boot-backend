package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryPartnerDto {
    private Long id;
    private UserDto user;
    private BigDecimal rating;
    private boolean isAvailable;
    private String vehicleId;
}