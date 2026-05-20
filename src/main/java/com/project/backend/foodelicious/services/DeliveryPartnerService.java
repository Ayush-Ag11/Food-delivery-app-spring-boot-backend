package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;

import java.util.List;

public interface DeliveryPartnerService {

    OrderDto acceptOrder(Long orderRequestId);

    OrderDto startOrder(Long orderId, String otp);

    OrderDto endOrder(Long orderId);

    OrderDto cancelOrder(Long orderId);

    CustomerDto rateCustomer(RatingRequestDto ratingRequestDto);

    DeliveryPartnerDto updateLocation(Double latitude, Double longitude);

    DeliveryPartnerDto toggleAvailability();

    List<OrderDto> getMyOrders();

    WalletDto getMyWallet();
}