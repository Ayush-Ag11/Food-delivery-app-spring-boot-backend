package com.project.backend.foodelicious.configs;

import com.project.backend.foodelicious.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Wires UserService + PasswordEncoder into Spring Security's auth system
    // This is what makes Spring Security use our loadUserByUsername() during login
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}