package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.strategies.DeliveryFeeCalculationStrategy;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DistanceBasedDeliveryFeeStrategy implements DeliveryFeeCalculationStrategy {

    // Fee slabs based on distance
    private static final BigDecimal FEE_UNDER_2KM = new BigDecimal("20.00");
    private static final BigDecimal FEE_UNDER_5KM = new BigDecimal("40.00");
    private static final BigDecimal FEE_UNDER_10KM = new BigDecimal("60.00");
    private static final BigDecimal FEE_ABOVE_10KM = new BigDecimal("80.00");

    @Override
    public BigDecimal calculateDeliveryFee(OrderRequest orderRequest) {

        // Get restaurant location
        Point restaurantLocation = orderRequest.getRestaurant().getLocation();

        // For now we use restaurant location as proxy for delivery distance
        // In enhancement phase — use actual customer delivery address coordinates
        // Distance calculated using Haversine formula approximation
        // PostGIS ST_Distance returns distance in meters for geography type
        // Here we use a simplified straight-line distance
        double distanceInKm = getDistanceInKm(restaurantLocation);

        if (distanceInKm <= 2) {
            return FEE_UNDER_2KM;
        } else if (distanceInKm <= 5) {
            return FEE_UNDER_5KM;
        } else if (distanceInKm <= 10) {
            return FEE_UNDER_10KM;
        } else {
            return FEE_ABOVE_10KM;
        }
    }

    // Simplified distance calculation
    // In enhancement phase — replace with actual customer address Point
    private double getDistanceInKm(Point restaurantLocation) {
        // Default city center coordinates — Meerut
        double cityLat = 28.9845;
        double cityLng = 77.7064;

        double lat = restaurantLocation.getY();
        double lng = restaurantLocation.getX();

        // Haversine formula
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat - cityLat);
        double dLng = Math.toRadians(lng - cityLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(cityLat))
                * Math.cos(Math.toRadians(lat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}