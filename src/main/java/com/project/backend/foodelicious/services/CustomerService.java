package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.request.CustomerAddressRequestDto;
import com.project.backend.foodelicious.dtos.request.PlaceOrderRequestDto;
import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerAddressDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.OrderRequestDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.entities.User;

import java.util.List;

public interface CustomerService {

    void createNewCustomer(User user);

    OrderRequestDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto);

    OrderDto cancelOrder(Long orderId);

    DeliveryPartnerDto rateDeliveryPartner(RatingRequestDto ratingRequestDto);

    WalletDto getMyWallet();

    List<OrderDto> getMyOrders();

    CustomerAddressDto addNewAddress(CustomerAddressRequestDto requestDto);

    List<CustomerAddressDto> getMyAddresses();

    List<RestaurantDto> getNearbyRestaurants(Double latitude, Double longitude);
}