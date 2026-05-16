package com.project.backend.foodelicious.advices;

import com.project.backend.foodelicious.exceptions.InsufficientBalanceException;
import com.project.backend.foodelicious.exceptions.ResourceNotFoundException;
import com.project.backend.foodelicious.exceptions.RuntimeConflictException;
import com.project.backend.foodelicious.exceptions.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeConflictException(
            RuntimeConflictException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(
            AuthenticationException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
            AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(ApiResponse.failure(apiError), apiError.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        // Collect all field validation errors into one message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return buildErrorResponseEntity(apiError);
    }
}