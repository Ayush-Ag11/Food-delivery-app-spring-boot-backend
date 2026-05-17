package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.request.CustomerAddressRequestDto;
import com.project.backend.foodelicious.dtos.request.PlaceOrderRequestDto;
import com.project.backend.foodelicious.dtos.request.RatingRequestDto;
import com.project.backend.foodelicious.dtos.response.CustomerAddressDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.OrderRequestDto;
import com.project.backend.foodelicious.dtos.response.RestaurantDto;
import com.project.backend.foodelicious.dtos.response.WalletDto;
import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.CustomerAddress;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.CustomerAddressRepository;
import com.project.backend.foodelicious.repositories.CustomerRepository;
import com.project.backend.foodelicious.repositories.RestaurantRepository;
import com.project.backend.foodelicious.services.CustomerService;
import com.project.backend.foodelicious.services.OrderService;
import com.project.backend.foodelicious.services.RatingService;
import com.project.backend.foodelicious.services.WalletService;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderService orderService;
    private final RatingService ratingService;
    private final WalletService walletService;
    private final ModelMapper modelMapper;

    // GeometryFactory for creating PostGIS Points
    // SRID 4326 = standard GPS coordinate system
    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public void createNewCustomer(User user) {
        Customer customer = Customer.builder()
                .user(user)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public OrderRequestDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto) {
        return orderService.placeOrder(placeOrderRequestDto);
    }

    @Override
    public OrderDto cancelOrder(Long orderId) {
        return orderService.cancelOrderByCustomer(orderId);
    }

    @Override
    public DeliveryPartnerDto rateDeliveryPartner(RatingRequestDto ratingRequestDto) {
        return ratingService.rateDeliveryPartner(
                ratingRequestDto.getOrderId(),
                ratingRequestDto.getRating()
        );
    }

    @Override
    public WalletDto getMyWallet() {
        User currentUser = getCurrentUser();
        return walletService.getMyWallet(currentUser);
    }

    @Override
    public List<OrderDto> getMyOrders() {
        User currentUser = getCurrentUser();
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user: " + currentUser.getId()
                ));
        return orderService.getOrdersByCustomer(customer);
    }

    @Override
    @Transactional
    public CustomerAddressDto addNewAddress(CustomerAddressRequestDto requestDto) {
        User currentUser = getCurrentUser();
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user: " + currentUser.getId()
                ));

        // If this is marked as default, unset previous default
        if (requestDto.isDefault()) {
            customerAddressRepository
                    .findByCustomerAndIsDefault(customer, true)
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        customerAddressRepository.save(existing);
                    });
        }

        // Convert lat/lng to PostGIS Point
        Point location = geometryFactory.createPoint(
                new Coordinate(requestDto.getLongitude(), requestDto.getLatitude())
        );

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .addressLabel(requestDto.getLabel())
                .houseNumber(requestDto.getHouseNumber())
                .street(requestDto.getStreet())
                .landmark(requestDto.getLandmark())
                .city(requestDto.getCity())
                .state(requestDto.getState())
                .pinCode(requestDto.getPincode())
                .country(requestDto.getCountry())
                .location(location)
                .isDefault(requestDto.isDefault())
                .build();

        CustomerAddress saved = customerAddressRepository.save(address);
        return modelMapper.map(saved, CustomerAddressDto.class);
    }

    @Override
    public List<CustomerAddressDto> getMyAddresses() {
        User currentUser = getCurrentUser();
        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user: " + currentUser.getId()
                ));
        return customerAddressRepository.findByCustomer(customer)
                .stream()
                .map(address -> modelMapper.map(address, CustomerAddressDto.class))
                .toList();
    }

    @Override
    public List<RestaurantDto> getNearbyRestaurants(Double latitude, Double longitude) {
        // Search within 5km radius
        double radiusInMeters = 5000.0;
        return restaurantRepository
                .findOpenRestaurantsNearby(latitude, longitude, radiusInMeters)
                .stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantDto.class))
                .toList();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }
}