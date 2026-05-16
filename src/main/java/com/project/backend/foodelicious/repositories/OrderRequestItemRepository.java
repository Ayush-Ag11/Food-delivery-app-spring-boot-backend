package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.entities.OrderRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRequestItemRepository extends JpaRepository<OrderRequestItem, Long> {

    List<OrderRequestItem> findByOrderRequest(OrderRequest orderRequest);
}
