package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantDto {
    private Long id;
    private UserDto owner;
    private String name;
    private String address;
    private BigDecimal rating;
    private boolean isOpen;
}