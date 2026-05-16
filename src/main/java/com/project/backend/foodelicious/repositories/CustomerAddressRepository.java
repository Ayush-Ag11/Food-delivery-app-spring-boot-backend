package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomer(Customer customer);

    Optional<CustomerAddress> findByCustomerAndIsDefault(Customer customer, boolean isDefault);
}
