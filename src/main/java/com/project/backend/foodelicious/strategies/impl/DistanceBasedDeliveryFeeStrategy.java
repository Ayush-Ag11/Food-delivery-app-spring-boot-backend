package com.project.backend.foodelicious.strategies.impl;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.services.OsrmService;
import com.project.backend.foodelicious.strategies.DeliveryFeeCalculationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class DistanceBasedDeliveryFeeStrategy implements DeliveryFeeCalculationStrategy {

    // Fee slabs based on distance
    private static final BigDecimal FEE_UNDER_2KM = new BigDecimal("20.00");
    private static final BigDecimal FEE_UNDER_5KM = new BigDecimal("40.00");
    private static final BigDecimal FEE_UNDER_10KM = new BigDecimal("60.00");
    private static final BigDecimal FEE_ABOVE_10KM = new BigDecimal("80.00");

    private final OsrmService osrmService;

    @Override
    public BigDecimal calculateDeliveryFee(OrderRequest orderRequest) {

        Point restaurantLocation = orderRequest.getRestaurant().getLocation();

        Point deliveryLocation = orderRequest.getDeliveryLocation();

       if(deliveryLocation == null) {
           return FEE_UNDER_5KM;
       }

       double resturantLat = restaurantLocation.getY();
       double deliveryLat = deliveryLocation.getY();
       double deliveryLon = deliveryLocation.getX();
       double resturantLon = restaurantLocation.getX();

       double distanceKm = osrmService.getRoadDistanceKm(resturantLat, resturantLon, deliveryLat, deliveryLon);

       log.info("Delivery fee calculation : road distance = {} km for order request {}",
               distanceKm, orderRequest.getId());

       if (distanceKm <= 2) return FEE_UNDER_2KM;
       if (distanceKm <= 5) return FEE_UNDER_5KM;
       if (distanceKm <= 10) return FEE_UNDER_10KM;
       return FEE_ABOVE_10KM;
    }
}