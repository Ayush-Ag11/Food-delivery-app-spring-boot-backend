package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.response.CustomerDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Rating;
import com.project.backend.foodelicious.entities.enums.OrderStatus;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.CustomerRepository;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.repositories.OrderRepository;
import com.project.backend.foodelicious.repositories.RatingRepository;
import com.project.backend.foodelicious.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createNewRating(Order order) {
        Rating rating = Rating.builder()
                .order(order)
                .deliveryPartner(order.getDeliveryPartner())
                .customer(order.getCustomer())
                .build();
        ratingRepository.save(rating);
    }

    @Override
    public DeliveryPartnerDto rateDeliveryPartner(Long orderId, Integer rating) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        // Can only rate after delivery
        if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new RuntimeException(
                    "Cannot rate delivery partner, order is not delivered yet"
            );
        }

        Rating ratingEntity = ratingRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rating not found for order with id: " + orderId
                ));

        if(ratingEntity.getDeliveryPartnerRating() != null) {
            throw new RuntimeException("Delivery partner rating already exists for order with id: " + orderId);
        }

        ratingEntity.setDeliveryPartnerRating(rating);
        ratingRepository.save(ratingEntity);

        // Recalculate delivery partner's average rating
        DeliveryPartner deliveryPartner = order.getDeliveryPartner();

        Double averageRating = ratingRepository
                .findAverageRatingOfDeliveryPartner(deliveryPartner)
                .orElse(0.0);

        deliveryPartner.setRating(
                BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP)
        );

        DeliveryPartner savedPartner = deliveryPartnerRepository.save(deliveryPartner);
        return modelMapper.map(savedPartner, DeliveryPartnerDto.class);
    }

    @Override
    public CustomerDto rateCustomer(Long orderId, Integer rating) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new RuntimeException(
                    "Cannot rate customer, order is not delivered yet"
            );
        }

        Rating ratingEntity = ratingRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rating not found for order with id: " + orderId
                ));

        if(ratingEntity.getCustomerRating() != null) {
            throw new RuntimeException("Customer rating already exists for order with id: " + orderId);
        }

        ratingEntity.setCustomerRating(rating);
        ratingRepository.save(ratingEntity);

        // Recalculate customer's average rating
        Customer customer = order.getCustomer();

        Double averageRating = ratingRepository
                .findAverageRatingOfCustomer(customer)
                .orElse(0.0);

        customer.setRating(
                BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP)
        );

        Customer savedCustomer = customerRepository.save(customer);
        return modelMapper.map(savedCustomer, CustomerDto.class);
    }
}