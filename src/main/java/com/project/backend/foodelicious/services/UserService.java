package com.project.backend.foodelicious.services;

import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // Called by Spring Security during login
    // Loads user by email (email is our username)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));
    }

    // Called by JwtAuthFilter on every authenticated request
    // Loads user by ID extracted from JWT token
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));
    }

    // Helper used across all services to get current logged-in User entity
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));
    }
}