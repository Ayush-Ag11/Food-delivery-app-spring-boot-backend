package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.repositories.OrderRepository;
import com.project.backend.foodelicious.services.DeliveryPartnerService;
import com.project.backend.foodelicious.services.OrderService;
import com.project.backend.foodelicious.services.RatingService;
import com.project.backend.foodelicious.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final RatingService ratingService;
    private final WalletService walletService;
    private final ModelMapper modelMapper;

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public OrderDto acceptOrder(Long orderRequestId) {
        return orderService.acceptOrder(orderRequestId);
    }

    @Override
    public OrderDto startOrder(Long orderId, String otp) {
        return orderService.startOrder(orderId, otp);
    }

    @Override
    public OrderDto endOrder(Long orderId) {
        return orderService.endOrder(orderId);
    }

    @Override
    public OrderDto cancelOrder(Long orderId) {
        return orderService.cancelOrderByDeliveryPartner(orderId);
    }

    @Override
    public CustomerDto rateCustomer(RatingRequestDto ratingRequestDto) {
        return ratingService.rateCustomer(
                ratingRequestDto.getOrderId(),
                ratingRequestDto.getRating()
        );
    }

    @Override
    public DeliveryPartnerDto updateLocation(Double latitude, Double longitude) {
        User currentUser = getCurrentUser();
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        // Convert lat/lng to PostGIS Point
        Point location = geometryFactory.createPoint(
                new Coordinate(longitude, latitude)  // longitude = x, latitude = y
        );

        deliveryPartner.setCurrentLocation(location);
        DeliveryPartner saved = deliveryPartnerRepository.save(deliveryPartner);
        return modelMapper.map(saved, DeliveryPartnerDto.class);
    }

    @Override
    public DeliveryPartnerDto toggleAvailability() {
        User currentUser = getCurrentUser();
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        deliveryPartner.setAvailable(!deliveryPartner.isAvailable());
        DeliveryPartner saved = deliveryPartnerRepository.save(deliveryPartner);
        return modelMapper.map(saved, DeliveryPartnerDto.class);
    }

    @Override
    public List<OrderDto> getMyOrders() {
        User currentUser = getCurrentUser();
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));
        return orderService.getOrdersByDeliveryPartner(deliveryPartner);
    }

    @Override
    public WalletDto getMyWallet() {
        User currentUser = getCurrentUser();
        return walletService.getMyWallet(currentUser);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }
}