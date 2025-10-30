package com.example.vestigioapi.dto.auth;

import com.example.vestigioapi.util.ValidationMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    String email,

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    String password
) {}
