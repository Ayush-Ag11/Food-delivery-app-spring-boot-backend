package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.MenuItem;
import com.project.backend.foodelicious.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurant(Restaurant restaurant);

    List<MenuItem> findByRestaurantAndIsAvailable(Restaurant restaurant, boolean isAvailable);
}
