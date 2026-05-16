package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByOrder(Order order);

    List<Rating> findByDeliveryPartner(DeliveryPartner deliveryPartner);

    List<Rating> findByCustomer(Customer customer);

    // Calculate average rating for a delivery partner
    // Only considers rows where deliveryPartnerRating is not null
    // (rating row is created empty when order starts, filled in later)
    @Query("""
            SELECT AVG(r.deliveryPartnerRating)
            FROM Rating r
            WHERE r.deliveryPartner = :deliveryPartner
            AND r.deliveryPartnerRating IS NOT NULL
            """)
    Optional<Double> findAverageRatingOfDeliveryPartner(
            @Param("deliveryPartner") DeliveryPartner deliveryPartner
    );

    // Calculate average rating for a customer
    @Query("""
            SELECT AVG(r.customerRating)
            FROM Rating r
            WHERE r.customer = :customer
            AND r.customerRating IS NOT NULL
            """)
    Optional<Double> findAverageRatingOfCustomer(
            @Param("customer") Customer customer
    );
}