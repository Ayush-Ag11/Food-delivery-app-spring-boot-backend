package com.project.backend.foodelicious.dtos.request;

import com.project.backend.foodelicious.entities.enums.AddressLabel;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerAddressRequestDto {

    @NotNull(message = "Label is required")
    private AddressLabel label;

    @NotBlank(message = "House number is required")
    private String houseNumber;

    @NotBlank(message = "Street is required")
    private String street;

    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Pincode must be a valid 6-digit Indian pincode")
    private String pincode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    private boolean isDefault;
}