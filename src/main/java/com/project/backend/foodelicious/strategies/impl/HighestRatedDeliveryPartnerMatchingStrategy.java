package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.strategies.DeliveryPartnerMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HighestRatedDeliveryPartnerMatchingStrategy implements DeliveryPartnerMatchingStrategy {

    private static final double RADIUS_IN_METERS = 5000.0;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @Override
    public List<DeliveryPartner> findMatchingDeliveryPartner(OrderRequest orderRequest) {

        double latitude = orderRequest.getRestaurant().getLocation().getY();
        double longitude = orderRequest.getRestaurant().getLocation().getX();

        // Get all available partners within radius
        List<DeliveryPartner> partners = deliveryPartnerRepository
                .findAvailablePartnersWithinRadius(latitude, longitude, RADIUS_IN_METERS);

        // Sort by rating descending — highest rated first
        // Partners with null rating go to the end
        return partners.stream()
                .sorted(Comparator.comparing(
                        DeliveryPartner::getRating,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
}