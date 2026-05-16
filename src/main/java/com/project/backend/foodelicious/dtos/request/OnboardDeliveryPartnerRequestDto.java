package com.project.backend.foodelicious.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OnboardDeliveryPartnerRequestDto {

    @NotBlank(message = "Vehicle ID is required")
    @Pattern(
            regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$",
            message = "Vehicle ID must be a valid Indian registration number e.g. UP14AB1234"
    )
    private String vehicleId;
}