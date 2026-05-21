package com.project.backend.foodelicious.controllers;

import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.services.DeliveryPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deliveryPartner")
@Secured("ROLE_DELIVERY_PARTNER")
@RequiredArgsConstructor
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    @PostMapping("/acceptOrder/{orderRequestId}")
    public ResponseEntity<OrderDto> acceptOrder(@PathVariable Long orderRequestId) {
        OrderDto orderDto = deliveryPartnerService.acceptOrder(orderRequestId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/startOrder/{orderId}")
    public ResponseEntity<OrderDto> startOrder(@PathVariable Long orderId, @RequestParam String otp) {
        OrderDto orderDto = deliveryPartnerService.startOrder(orderId, otp);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/endOrder/{orderId}")
    public ResponseEntity<OrderDto> endOrder(@PathVariable Long orderId) {
        OrderDto orderDto = deliveryPartnerService.endOrder(orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/cancelOrder/{orderId}")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId) {
        OrderDto orderDto = deliveryPartnerService.cancelOrder(orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/rateCustomer")
    public ResponseEntity<CustomerDto> rateCustomer(@Valid @RequestBody RatingRequestDto ratingRequestDto) {
        CustomerDto customerDto = deliveryPartnerService.rateCustomer(ratingRequestDto);
        return ResponseEntity.ok(customerDto);
    }

    @PatchMapping("/updateLocation")
    public ResponseEntity<DeliveryPartnerDto> updateLocation(@RequestParam Double latitude,
                                                             @RequestParam Double longitude) {
        DeliveryPartnerDto deliveryPartnerDto = deliveryPartnerService.updateLocation(latitude, longitude);
        return ResponseEntity.ok(deliveryPartnerDto);
    }

    @PatchMapping("/toggleAvailability")
    public ResponseEntity<DeliveryPartnerDto> toggleAvailability() {
        DeliveryPartnerDto deliveryPartnerDto = deliveryPartnerService.toggleAvailability();
        return ResponseEntity.ok(deliveryPartnerDto);
    }

    @GetMapping("/getMyOrders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        List<OrderDto> orders = deliveryPartnerService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/getMyWallet")
    public ResponseEntity<WalletDto> getMyWallet() {
        WalletDto walletDto = deliveryPartnerService.getMyWallet();
        return ResponseEntity.ok(walletDto);
    }
}
