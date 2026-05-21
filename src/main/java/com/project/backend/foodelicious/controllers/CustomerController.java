package com.project.backend.foodelicious.controllers;

import com.project.backend.foodelicious.dtos.request.CustomerAddressRequestDto;
import com.project.backend.foodelicious.dtos.request.PlaceOrderRequestDto;
import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerAddressDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.OrderRequestDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
@Secured("ROLE_CUSTOMER")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderRequestDto> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDto placeOrderRequestDto) {
        OrderRequestDto orderRequestDto = customerService.placeOrder(placeOrderRequestDto);
        return new ResponseEntity<>(orderRequestDto, HttpStatus.CREATED);
    }

    @PostMapping("/cancelOrder/{orderId}")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId) {
        OrderDto orderDto = customerService.cancelOrder(orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/rateDeliveryPartner")
    public ResponseEntity<DeliveryPartnerDto> rateDeliveryPartner(
            @Valid @RequestBody RatingRequestDto ratingRequestDto) {
        DeliveryPartnerDto deliveryPartnerDto =
                customerService.rateDeliveryPartner(ratingRequestDto);
        return ResponseEntity.ok(deliveryPartnerDto);
    }

    @GetMapping("/getMyWallet")
    public ResponseEntity<WalletDto> getMyWallet() {
        WalletDto walletDto = customerService.getMyWallet();
        return ResponseEntity.ok(walletDto);
    }

    @GetMapping("/getMyOrders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        List<OrderDto> orders = customerService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/addAddress")
    public ResponseEntity<CustomerAddressDto> addNewAddress(
            @Valid @RequestBody CustomerAddressRequestDto requestDto) {
        CustomerAddressDto addressDto = customerService.addNewAddress(requestDto);
        return new ResponseEntity<>(addressDto, HttpStatus.CREATED);
    }

    @GetMapping("/getMyAddresses")
    public ResponseEntity<List<CustomerAddressDto>> getMyAddresses() {
        List<CustomerAddressDto> addresses = customerService.getMyAddresses();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/getNearbyRestaurants")
    public ResponseEntity<List<RestaurantDto>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<RestaurantDto> restaurants =
                customerService.getNearbyRestaurants(latitude, longitude);
        return ResponseEntity.ok(restaurants);
    }
}