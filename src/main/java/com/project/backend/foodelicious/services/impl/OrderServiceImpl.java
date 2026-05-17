package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.request.PlaceOrderRequestDto;
import com.project.backend.foodelicious.dtos.response.OrderDto;
import com.project.backend.foodelicious.dtos.response.OrderRequestDto;
import com.project.backend.foodelicious.entities.Customer;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.MenuItem;
import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.OrderItem;
import com.project.backend.foodelicious.entities.OrderRequest;
import com.project.backend.foodelicious.entities.OrderRequestItem;
import com.project.backend.foodelicious.entities.Restaurant;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.enums.OrderRequestStatus;
import com.project.backend.foodelicious.entities.enums.OrderStatus;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.exceptions.UnauthorizedAccessException;
import com.project.backend.foodelicious.repositories.CustomerRepository;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.repositories.MenuItemRepository;
import com.project.backend.foodelicious.repositories.OrderItemRepository;
import com.project.backend.foodelicious.repositories.OrderRepository;
import com.project.backend.foodelicious.repositories.OrderRequestItemRepository;
import com.project.backend.foodelicious.repositories.OrderRequestRepository;
import com.project.backend.foodelicious.repositories.RestaurantRepository;
import com.project.backend.foodelicious.services.OrderService;
import com.project.backend.foodelicious.services.PaymentService;
import com.project.backend.foodelicious.services.RatingService;
import com.project.backend.foodelicious.strategies.DeliveryPartnerMatchingStrategyManager;
import com.project.backend.foodelicious.strategies.OrderAssignmentStrategyManager;
import com.project.backend.foodelicious.strategies.OrderFareCalculationStrategyManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderRequestRepository orderRequestRepository;
    private final OrderRequestItemRepository orderRequestItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentService paymentService;
    private final RatingService ratingService;
    private final OrderFareCalculationStrategyManager fareStrategyManager;
    private final DeliveryPartnerMatchingStrategyManager matchingStrategyManager;
    private final OrderAssignmentStrategyManager assignmentStrategyManager;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderRequestDto placeOrder(PlaceOrderRequestDto requestDto) {

        // Get current logged-in customer
        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user with id: " + currentUser.getId()
                ));

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Restaurant not found with id: " + requestDto.getRestaurantId()
                ));

        if (!restaurant.isOpen()) {
            throw new RuntimeException(
                    "Restaurant is currently closed: " + restaurant.getName()
            );
        }

        // Build OrderRequest
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomer(customer);
        orderRequest.setRestaurant(restaurant);
        orderRequest.setDeliveryAddress(requestDto.getDeliveryAddress());
        orderRequest.setPaymentMethod(requestDto.getPaymentMethod());
        orderRequest.setOrderRequestStatus(OrderRequestStatus.PENDING);

        OrderRequest savedRequest = orderRequestRepository.save(orderRequest);

        // Build OrderRequestItems
        List<OrderRequestItem> items = requestDto.getItems().stream()
                .map(itemRequest -> {
                    MenuItem menuItem = menuItemRepository
                            .findById(itemRequest.getMenuItemId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Menu item not found with id: " + itemRequest.getMenuItemId()
                            ));

                    if (!menuItem.isAvailable()) {
                        throw new RuntimeException(
                                "Menu item is not available: " + menuItem.getName()
                        );
                    }

                    return OrderRequestItem.builder()
                            .orderRequest(savedRequest)
                            .menuItem(menuItem)
                            .quantity(itemRequest.getQuantity())
                            .build();
                })
                .toList();

        orderRequestItemRepository.saveAll(items);
        savedRequest.setItems(items);

        // Calculate fare using strategy
        BigDecimal totalFare = fareStrategyManager
                .getFareCalculationStrategy()
                .calculateFare(savedRequest);

        savedRequest.setTotalFare(totalFare);
        orderRequestRepository.save(savedRequest);

        // Find matching delivery partners using strategy
        List<DeliveryPartner> matchedPartners = matchingStrategyManager
                .getMatchingStrategy()
                .findMatchingDeliveryPartner(savedRequest);

        // Assign/notify partners using strategy
        assignmentStrategyManager
                .getAssignmentStrategy()
                .assignOrder(savedRequest, matchedPartners);

        return modelMapper.map(savedRequest, OrderRequestDto.class);
    }

    @Override
    @Transactional
    public OrderDto acceptOrder(Long orderRequestId) {

        // Get current logged-in delivery partner
        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        if (!deliveryPartner.isAvailable()) {
            throw new RuntimeException(
                    "Delivery partner is not available to accept orders"
            );
        }

        OrderRequest orderRequest = orderRequestRepository.findById(orderRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order request not found with id: " + orderRequestId
                ));

        if (!orderRequest.getOrderRequestStatus().equals(OrderRequestStatus.PENDING)) {
            throw new RuntimeException(
                    "Order request is not pending, current status: "
                            + orderRequest.getOrderRequestStatus()
            );
        }

        // Mark partner as unavailable
        deliveryPartner.setAvailable(false);
        deliveryPartnerRepository.save(deliveryPartner);

        // Update order request status
        orderRequest.setOrderRequestStatus(OrderRequestStatus.CONFIRMED);
        orderRequestRepository.save(orderRequest);

        // Create Order from OrderRequest
        Order order = new Order();
        order.setCustomer(orderRequest.getCustomer());
        order.setDeliveryPartner(deliveryPartner);
        order.setRestaurant(orderRequest.getRestaurant());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setTotalFare(orderRequest.getTotalFare());
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setOtp(generateOtp());

        Order savedOrder = orderRepository.save(order);

        // Copy items from OrderRequest to Order as OrderItems (price snapshot)
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(requestItem -> OrderItem.builder()
                        .order(savedOrder)
                        .menuItem(requestItem.getMenuItem())
                        .quantity(requestItem.getQuantity())
                        .priceAtTimeOfOrder(requestItem.getMenuItem().getPrice())
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto startOrder(Long orderId, String otp) {

        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        // Verify this partner owns this order
        if (!order.getDeliveryPartner().equals(deliveryPartner)) {
            throw new UnauthorizedAccessException(
                    "Delivery partner does not own this order with id: " + orderId
            );
        }

        if (!order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new RuntimeException(
                    "Order is not in CONFIRMED status, current status: "
                            + order.getOrderStatus()
            );
        }

        // Verify OTP
        if (!order.getOtp().equals(otp)) {
            throw new RuntimeException(
                    "Invalid OTP provided: " + otp
            );
        }

        order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setStartedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Create pending payment record
        paymentService.createNewPayment(savedOrder);

        // Create empty rating record
        ratingService.createNewRating(savedOrder);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto endOrder(Long orderId) {

        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        if (!order.getDeliveryPartner().equals(deliveryPartner)) {
            throw new UnauthorizedAccessException(
                    "Delivery partner does not own this order with id: " + orderId
            );
        }

        if (!order.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY)) {
            throw new RuntimeException(
                    "Order is not OUT_FOR_DELIVERY, current status: "
                            + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Mark delivery partner as available again
        deliveryPartner.setAvailable(true);
        deliveryPartnerRepository.save(deliveryPartner);

        // Process payment
        paymentService.processPayment(savedOrder);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto cancelOrderByCustomer(Long orderId) {

        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Customer customer = customerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user: " + currentUser.getId()
                ));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        if (!order.getCustomer().equals(customer)) {
            throw new UnauthorizedAccessException(
                    "Customer does not own this order with id: " + orderId
            );
        }

        // Can only cancel CONFIRMED orders — not ones already picked up
        if (!order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new RuntimeException(
                    "Cannot cancel order in status: " + order.getOrderStatus()
                            + ". Can only cancel CONFIRMED orders"
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Free up the delivery partner
        DeliveryPartner deliveryPartner = order.getDeliveryPartner();
        deliveryPartner.setAvailable(true);
        deliveryPartnerRepository.save(deliveryPartner);

        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto cancelOrderByDeliveryPartner(Long orderId) {

        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery partner not found for user: " + currentUser.getId()
                ));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        if (!order.getDeliveryPartner().equals(deliveryPartner)) {
            throw new UnauthorizedAccessException(
                    "Delivery partner does not own this order with id: " + orderId
            );
        }

        if (!order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new RuntimeException(
                    "Cannot cancel order in status: " + order.getOrderStatus()
                            + ". Can only cancel CONFIRMED orders"
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Free up the delivery partner
        deliveryPartner.setAvailable(true);
        deliveryPartnerRepository.save(deliveryPartner);

        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer)
                .stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
    }

    @Override
    public List<OrderDto> getOrdersByDeliveryPartner(DeliveryPartner deliveryPartner) {
        return orderRepository.findByDeliveryPartner(deliveryPartner)
                .stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(9000) + 1000; // always 4 digits
        return String.valueOf(otp);
    }
}