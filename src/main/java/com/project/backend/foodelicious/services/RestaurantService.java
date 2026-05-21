package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.request.CreateMenuItemRequestDto;
import com.project.backend.foodelicious.dtos.request.CreateRestaurantRequestDto;
import com.project.backend.foodelicious.dtos.response.MenuItemDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;

import java.util.List;

public interface RestaurantService {

    RestaurantDto createRestaurant(CreateRestaurantRequestDto restaurantDto);

    MenuItemDto addMenuItem(Long restaurantId, CreateMenuItemRequestDto requestDto);

    MenuItemDto updateMenuItem(Long menuItemId, CreateMenuItemRequestDto requestDto);

    void deleteMenuItem(Long menuItemId);

    RestaurantDto toggleRestaurantAvailability(Long restaurantId);

    List<MenuItemDto> getMenuItems(Long restaurantId);

    List<RestaurantDto> getMyRestaurants();
}
