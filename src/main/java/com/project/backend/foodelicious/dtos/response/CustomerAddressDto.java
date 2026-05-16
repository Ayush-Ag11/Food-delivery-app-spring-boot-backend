package com.project.backend.foodelicious.dtos.response;

import com.project.backend.foodelicious.entities.enums.AddressLabel;
import lombok.Data;

@Data
public class CustomerAddressDto {
    private Long id;
    private AddressLabel label;
    private String houseNumber;
    private String street;
    private String landmark;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private boolean isDefault;
}