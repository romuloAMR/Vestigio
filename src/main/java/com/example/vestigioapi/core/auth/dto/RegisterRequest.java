package com.example.vestigioapi.core.auth.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;
import com.example.vestigioapi.core.common.util.ValidationParam;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = ValidationMessages.NAME_REQUIRED)
    String name,
    
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    String email,
    
    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(min = ValidationParam.PASSWORD_MIN, message = ValidationMessages.PASSWORD_MIN)
    @Pattern(
        regexp = ValidationParam.PASSWORD_REGEX,
        message = ValidationMessages.PASSWORD_PATTERN
    )
    String password
) {}
