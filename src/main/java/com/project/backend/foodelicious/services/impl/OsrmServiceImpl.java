package com.project.backend.foodelicious.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.foodelicious.services.OsrmService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OsrmServiceImpl implements OsrmService {

    private final RestTemplate restTemplate;

    @Value("${osrm.base-url}")
    private String osrmBaseUrl;


    @Override
    public double getRoadDistanceKm(double fromLat, double fromLon, double toLat, double toLon) {
        String url = String.format("%s/route/v1/driving/%f,%f:%f,%f?overview=false",
                osrmBaseUrl, fromLat, fromLon, toLat, toLon);

        try {
            OsrmResponse osrmResponse = restTemplate.getForObject(url, OsrmResponse.class);

            if (osrmResponse != null
                    && "Ok".equals(osrmResponse.getCode())
                    && osrmResponse.getRoutes() != null
                    && !osrmResponse.getRoutes().isEmpty()
                    && osrmResponse.getRoutes().get(0) != null) {

                double distanceMetres = osrmResponse.getRoutes().get(0).getDistance();
                double distanceKm = distanceMetres / 1000;

                log.debug("OSRM road distance from ({},{}) to ({},{}): {} km", fromLat, fromLon, toLat, toLon, distanceKm);

                return distanceKm;
            }

            log.warn("OSRM returned non-Ok code '{}' for route ({},{}) -> ({},{}). " +
                            "Falling back to Haversine.", osrmResponse != null ? osrmResponse.getCode() : "null",
                    fromLat, fromLon, toLat, toLon);
        } catch (Exception e) {

            log.warn("OSRM call failed for route ({},{}) -> ({},{}): {}. " +
                            "Falling back to Haversine straight-line distance.",
                    fromLat, fromLon, toLat, toLon, e.getMessage());
        }

        double haversineKm = haversineDistanceKm(fromLat, fromLon, toLat, toLon);
        log.info("Using Haversine fallback with 1.3x road factor: {} km -> {} km", haversineKm, haversineKm * 1.3);
        return haversineKm;
    }

    private double haversineDistanceKm(double fromLat, double fromLon, double toLat, double toLon) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(toLat - fromLat);
        double dLon = Math.toRadians(toLon - fromLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat))
                * Math.cos(Math.toRadians(toLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OsrmResponse {

        @JsonProperty("code")
        private String code;

        @JsonProperty("routes")
        private List<OsrmRoute> routes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OsrmRoute {

        @JsonProperty("distance")
        private double distance;

        @JsonProperty("duration")
        private double duration;
    }
}
