package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemDto {
    private Long id;
    private Long restaurantId;      // just the ID, not the full RestaurantDto
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;
}