package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.entities.Restaurant;
import com.project.backend.foodelicious.entities.enums.OrderRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {

    List<OrderRequest> findByCustomer(Customer customer);

    List<OrderRequest> findByRestaurant(Restaurant restaurant);

    // Delivery partners poll for pending requests they can accept
    List<OrderRequest> findByOrderRequestStatus(OrderRequestStatus status);

    // Find the active pending request for a customer
    // A customer should not have two pending requests at the same time
    Optional<OrderRequest> findByCustomerAndOrderRequestStatus(
            Customer customer,
            OrderRequestStatus status
    );
}
