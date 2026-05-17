package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.request.PlaceOrderRequestDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.OrderRequestDto;
import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.DeliveryPartner;

import java.util.List;

public interface OrderService {

    OrderRequestDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto);

    OrderDto acceptOrder(Long orderRequestId);

    OrderDto startOrder(Long orderId, String otp);

    OrderDto endOrder(Long orderId);

    OrderDto cancelOrderByCustomer(Long orderId);

    OrderDto cancelOrderByDeliveryPartner(Long orderId);

    List<OrderDto> getOrdersByCustomer(Customer customer);

    List<OrderDto> getOrdersByDeliveryPartner(DeliveryPartner deliveryPartner);

}