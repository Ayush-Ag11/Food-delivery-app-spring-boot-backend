package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.dtos.request.LoginRequestDto;
import com.project.backend.foodelicious.dtos.request.OnboardDeliveryPartnerRequestDto;
import com.project.backend.foodelicious.dtos.request.SignUpRequestDto;
import com.project.backend.foodelicious.dtos.response.DeliveryPartnerDto;
import com.project.backend.foodelicious.dtos.response.LoginResponseDto;
import com.project.backend.foodelicious.dtos.response.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserDto signUp(SignUpRequestDto signUpRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);

    DeliveryPartnerDto onboardDeliveryPartner(Long userId,
                                              OnboardDeliveryPartnerRequestDto onboardDeliveryPartnerRequestDto);

    LoginResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response);
}