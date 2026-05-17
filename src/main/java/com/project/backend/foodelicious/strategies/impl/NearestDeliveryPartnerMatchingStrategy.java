package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.strategies.DeliveryPartnerMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NearestDeliveryPartnerMatchingStrategy implements DeliveryPartnerMatchingStrategy {

    // Search radius — 5km from restaurant location
    private static final double RADIUS_IN_METERS = 5000.0;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @Override
    public List<DeliveryPartner> findMatchingDeliveryPartner(OrderRequest orderRequest) {

        // Get restaurant location coordinates
        double latitude = orderRequest.getRestaurant().getLocation().getY();
        double longitude = orderRequest.getRestaurant().getLocation().getX();

        // Use PostGIS spatial query to find nearest available partners
        // Results are already ordered by distance (closest first)
        return deliveryPartnerRepository.findAvailablePartnersWithinRadius(
                latitude,
                longitude,
                RADIUS_IN_METERS
        );
    }
}