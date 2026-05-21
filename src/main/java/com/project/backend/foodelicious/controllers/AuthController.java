package com.project.backend.foodelicious.controllers;

import com.project.backend.foodelicious.dtos.request.LoginRequestDto;
import com.project.backend.foodelicious.dtos.request.OnboardDeliveryPartnerRequestDto;
import com.project.backend.foodelicious.dtos.request.SignUpRequestDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.LoginResponseDto;
import com.project.backend.foodelicious.dtos.response.UserDto;
import com.project.backend.foodelicious.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        UserDto userDto = authService.signUp(signUpRequestDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto, response);
        return ResponseEntity.ok(loginResponseDto);
    }

    // Only ADMIN can onboard a new delivery partner
    @Secured("ROLE_ADMIN")
    @PostMapping("/onboardDeliveryPartner/{userId}")
    public ResponseEntity<DeliveryPartnerDto> onboardDeliveryPartner(
            @PathVariable Long userId,
            @Valid @RequestBody OnboardDeliveryPartnerRequestDto requestDto) {
        DeliveryPartnerDto deliveryPartnerDto =
                authService.onboardDeliveryPartner(userId, requestDto);
        return new ResponseEntity<>(deliveryPartnerDto, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.refreshToken(request, response);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }
}