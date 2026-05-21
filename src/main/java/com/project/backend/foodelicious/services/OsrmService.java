package com.project.backend.foodelicious.services;

public interface OsrmService {

    double getRoadDistanceKm(double fromLat, double fromLon, double toLat, double toLon);
}
