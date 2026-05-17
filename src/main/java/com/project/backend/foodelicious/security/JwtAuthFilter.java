package com.project.backend.foodelicious.security;

import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    // @Lazy breaks the circular dependency:
    // SecurityConfig → JwtAuthFilter → UserService → SecurityConfig
    @Autowired
    public JwtAuthFilter(JWTService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extract Authorization header
        final String requestTokenHeader = request.getHeader("Authorization");

        // If no token or doesn't start with Bearer, skip this filter
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip "Bearer " prefix to get raw token
        String token = requestTokenHeader.substring(7);

        // Extract user ID from token
        Long userId = jwtService.getUserIdFromToken(token);

        // If we got a userId and no auth is already set in context
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user from DB
            User user = (User) userService.loadUserById(userId);

            // Create authentication token with user's authorities
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            // Attach request details (IP address, session ID etc.)
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Set authentication in SecurityContext
            // From this point Spring Security knows who made this request
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}