package com.project.backend.foodelicious.controllers;

import com.project.backend.foodelicious.dtos.request.CreateMenuItemRequestDto;
import com.project.backend.foodelicious.dtos.request.CreateRestaurantRequestDto;
import com.project.backend.foodelicious.dtos.response.MenuItemDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;
import com.project.backend.foodelicious.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequestDto createRestaurantRequestDto) {
        RestaurantDto dto = restaurantService.createRestaurant(createRestaurantRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Secured("ROLE_RESTAURANT_OWNER")
    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemDto> addMenuItem(@PathVariable Long restaurantId,
                                                   @Valid @RequestBody CreateMenuItemRequestDto createMenuItemRequestDto) {
        MenuItemDto dto = restaurantService.addMenuItem(restaurantId, createMenuItemRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Secured("ROLE_RESTAURANT_OWNER")
    @PutMapping("/menu/{menuItemId}")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable Long menuItemId,
            @Valid @RequestBody CreateMenuItemRequestDto createMenuItemRequestDto) {
        MenuItemDto dto = restaurantService.updateMenuItem(menuItemId, createMenuItemRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured("ROLE_RESTAURANT_OWNER")
    @DeleteMapping("/menu/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long menuItemId) {
        restaurantService.deleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_RESTAURANT_OWNER")
    @PatchMapping("/{restaurantId}/toggle")
    public ResponseEntity<RestaurantDto> toggleAvailability(@PathVariable Long restaurantId) {
        RestaurantDto dto = restaurantService.toggleRestaurantAvailability(restaurantId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemDto>> getMenuItems(@PathVariable Long restaurantId) {
        List<MenuItemDto> Items = restaurantService.getMenuItems(restaurantId);
        return new ResponseEntity<>(Items, HttpStatus.OK);
    }

    @Secured("ROLE_RESTAURANT_OWNER")
    @GetMapping("/myRestaurants")
    public ResponseEntity<List<RestaurantDto>> getMyRestaurants() {
        List<RestaurantDto> Items = restaurantService.getMyRestaurants();
        return new ResponseEntity<>(Items, HttpStatus.OK);
    }
}
