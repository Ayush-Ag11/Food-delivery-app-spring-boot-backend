package com.project.backend.foodelicious.services.impl;

import com.project.backend.foodelicious.dtos.request.LoginRequestDto;
import com.project.backend.foodelicious.dtos.request.OnboardDeliveryPartnerRequestDto;
import com.project.backend.foodelicious.dtos.request.SignUpRequestDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.LoginResponseDto;
import com.project.backend.foodelicious.dtos.response.UserDto;
import com.project.backend.foodelicious.entities.DeliveryPartner;
import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.enums.Role;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.exceptions.RuntimeConflictException;
import com.project.backend.foodelicious.repositories.DeliveryPartnerRepository;
import com.project.backend.foodelicious.repositories.UserRepository;
import com.project.backend.foodelicious.security.JWTService;
import com.project.backend.foodelicious.services.AuthService;
import com.project.backend.foodelicious.services.CustomerService;
import com.project.backend.foodelicious.services.WalletService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final CustomerService customerService;
    private final WalletService walletService;

    @Override
    @Transactional
    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

        // Check email not already taken
        boolean exists = userRepository.existsByEmail(signUpRequestDto.getEmail());
        if (exists) {
            throw new RuntimeConflictException(
                    "User already exists with email: " + signUpRequestDto.getEmail()
            );
        }

        // Build and save User
        User user = User.builder()
                .name(signUpRequestDto.getName())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .roles(Set.of(Role.CUSTOMER))
                .build();

        User savedUser = userRepository.save(user);

        // Create associated Customer profile
        customerService.createNewCustomer(savedUser);

        // Create associated Wallet
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto,
                                  HttpServletResponse response) {

        // Authenticate using email and password
        // This internally calls loadUserByUsername() and checks password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Set refresh token as HttpOnly cookie
        // HttpOnly means JavaScript cannot access it — prevents XSS attacks
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30 * 6); // 6 months in seconds
        response.addCookie(refreshTokenCookie);

        return new LoginResponseDto(accessToken);
    }

    @Override
    @Transactional
    public DeliveryPartnerDto onboardDeliveryPartner(Long userId,
                                                     OnboardDeliveryPartnerRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        // Check not already a delivery partner
        if (user.getRoles().contains(Role.DELIVERY_PARTNER)) {
            throw new RuntimeConflictException(
                    "User with id " + userId + " is already a delivery partner"
            );
        }

        // Create DeliveryPartner profile
        DeliveryPartner deliveryPartner = DeliveryPartner.builder()
                .user(user)
                .vehicleId(requestDto.getVehicleId())
                .isAvailable(true)
                .build();

        DeliveryPartner savedPartner = deliveryPartnerRepository.save(deliveryPartner);

        // Add DELIVERY_PARTNER role to existing user roles
        user.getRoles().add(Role.DELIVERY_PARTNER);
        userRepository.save(user);

        // Create wallet for delivery partner
        walletService.createNewWallet(user);

        return modelMapper.map(savedPartner, DeliveryPartnerDto.class);
    }

    @Override
    public LoginResponseDto refreshToken(HttpServletRequest request,
                                         HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new ResourceNotFoundException("Refresh token not found inside cookies");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Refresh token not found inside cookies"
                ));

        // Extract user ID from refresh token
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDto(accessToken);
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}