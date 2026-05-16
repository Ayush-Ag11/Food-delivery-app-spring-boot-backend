package com.project.backend.foodelicious.dtos.response;

import com.project.backend.foodelicious.entities.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles;
}