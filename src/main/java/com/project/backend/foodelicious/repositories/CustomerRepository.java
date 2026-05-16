package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUser(User user);
}
