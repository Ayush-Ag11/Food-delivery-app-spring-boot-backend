package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Restaurant;
import com.project.backend.foodelicious.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByOwner(User owner);

    // Find all open restaurants within radius of customer's location
    @Query(value = """
            SELECT * FROM restaurant r
            WHERE r.is_open = true
            AND ST_DWithin(
                r.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radiusInMeters
            )
            ORDER BY ST_Distance(
                r.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
            )
            """, nativeQuery = true)
    List<Restaurant> findOpenRestaurantsNearby(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInMeters") double radiusInMeters
    );
}
