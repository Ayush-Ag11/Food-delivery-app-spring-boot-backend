package com.project.backend.foodelicious.dtos.request;

import com.project.backend.foodelicious.entities.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequestDto {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Data
    public static class OrderItemRequest {

        @NotNull(message = "Menu item ID is required")
        private Long menuItemId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}