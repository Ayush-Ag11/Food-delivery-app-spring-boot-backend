package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

@Data
public class OrderRequestItemDto {
    private Long id;
    private MenuItemDto menuItem;
    private Integer quantity;
}