package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner> findByUser(User user);

    // PostGIS spatial query — finds available delivery partners within radiusInMeters
    // ST_DWithin checks if currentLocation is within the given distance from the point
    // ST_MakePoint(longitude, latitude) constructs the restaurant's location point
    // ST_SetSRID sets the coordinate system to 4326 (standard GPS coordinates)
    @Query(value = """
            SELECT * FROM delivery_partner dp
            WHERE dp.is_available = true
            AND ST_DWithin(
                dp.current_location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radiusInMeters
            )
            ORDER BY ST_Distance(
                dp.current_location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
            )
            """, nativeQuery = true)
    List<DeliveryPartner> findAvailablePartnersWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInMeters") double radiusInMeters
    );
}
