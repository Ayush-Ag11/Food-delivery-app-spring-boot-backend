package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}
