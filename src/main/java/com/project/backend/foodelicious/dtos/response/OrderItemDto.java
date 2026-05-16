package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long id;
    private MenuItemDto menuItem;
    private Integer quantity;
    private BigDecimal priceAtTimeOfOrder;
}