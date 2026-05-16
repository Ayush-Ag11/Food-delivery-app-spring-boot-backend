package com.project.backend.foodelicious.dtos.response;

import lombok.Data;

@Data
public class RatingDto {
    private Long id;
    private Long orderId;               // just the ID
    private DeliveryPartnerDto deliveryPartner;
    private CustomerDto customer;
    private Integer deliveryPartnerRating;
    private Integer customerRating;
}