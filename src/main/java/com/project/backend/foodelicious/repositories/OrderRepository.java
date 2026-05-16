package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Restaurant;
import com.project.backend.foodelicious.entities.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(Customer customer);

    List<Order> findByDeliveryPartner(DeliveryPartner deliveryPartner);

    List<Order> findByRestaurant(Restaurant restaurant);

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Find active order for a delivery partner
    // A partner can only have one active order at a time
    Optional<Order> findByDeliveryPartnerAndOrderStatus(
            DeliveryPartner deliveryPartner,
            OrderStatus orderStatus
    );
}
