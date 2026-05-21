package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.request.CreateMenuItemRequestDto;
import com.project.backend.foodelicious.dtos.request.CreateRestaurantRequestDto;
import com.project.backend.foodelicious.dtos.response.MenuItemDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;
import com.project.backend.foodelicious.entities.MenuItem;
import com.project.backend.foodelicious.entities.Restaurant;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.enums.Role;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.exceptions.UnauthorizedAccessException;
import com.project.backend.foodelicious.repositories.MenuItemRepository;
import com.project.backend.foodelicious.repositories.RestaurantRepository;
import com.project.backend.foodelicious.repositories.UserRepository;
import com.project.backend.foodelicious.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional
    public RestaurantDto createRestaurant(CreateRestaurantRequestDto restaurantDto) {

        User currentUser = getCurrentUser();
        Point location = geometryFactory.createPoint(new Coordinate(restaurantDto.getLongitude(), restaurantDto.getLatitude()));

        Restaurant restaurant = Restaurant.builder()
                .owner(currentUser)
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .location(location)
                .isOpen(false)
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        if (!currentUser.getRoles().contains(Role.RESTAURANT_OWNER)) {
            currentUser.getRoles().add(Role.RESTAURANT_OWNER);
            userRepository.save(currentUser);
        }

        return modelMapper.map(savedRestaurant, RestaurantDto.class);
    }

    @Override
    @Transactional
    public MenuItemDto addMenuItem(Long restaurantId, CreateMenuItemRequestDto requestDto) {
        Restaurant restaurant = getRestaurantOwnedByCurrentUser(restaurantId);

        MenuItem menuItem = MenuItem.builder()
                .restaurant(restaurant)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .isAvailable(true)
                .build();

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(savedMenuItem, MenuItemDto.class);
    }

    @Override
    public MenuItemDto updateMenuItem(Long menuItemId, CreateMenuItemRequestDto requestDto) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("menu item not found with id: " + menuItemId));

        getRestaurantOwnedByCurrentUser(menuItem.getRestaurant().getId());

        menuItem.setName(requestDto.getName());
        menuItem.setDescription(requestDto.getDescription());
        menuItem.setPrice(requestDto.getPrice());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return modelMapper.map(savedMenuItem, MenuItemDto.class);
    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("menu item not found with id: " + menuItemId));

        getRestaurantOwnedByCurrentUser(menuItem.getRestaurant().getId());

        menuItemRepository.deleteById(menuItemId);
    }

    @Override
    @Transactional
    public RestaurantDto toggleRestaurantAvailability(Long restaurantId) {
        Restaurant restaurant = getRestaurantOwnedByCurrentUser(restaurantId);
        restaurant.setOpen(!restaurant.isOpen());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return modelMapper.map(savedRestaurant, RestaurantDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemDto> getMenuItems(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "restaurant not found with id: " + restaurantId
                ));
        return menuItemRepository.findByRestaurant(restaurant)
                .stream().map(item -> modelMapper.map(item, MenuItemDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDto> getMyRestaurants() {
        User currentUser = getCurrentUser();
        return restaurantRepository.findByOwner(currentUser)
                .stream()
                .map(r -> modelMapper.map(r, RestaurantDto.class))
                .toList();
    }

    private Restaurant getRestaurantOwnedByCurrentUser(Long restaurantId) {
        User currentUser = getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant with id: " + restaurantId + " not found."
                ));

        if (!restaurant.getOwner().equals(currentUser)) {
            throw new UnauthorizedAccessException(
                    "You do not own the restaurant with id: " + restaurantId
            );
        }
        return restaurant;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
