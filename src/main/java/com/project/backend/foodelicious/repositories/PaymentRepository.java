package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);
}
