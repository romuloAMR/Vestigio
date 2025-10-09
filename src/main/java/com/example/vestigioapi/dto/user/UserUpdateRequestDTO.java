package com.example.vestigioapi.dto.user;

import com.example.vestigioapi.util.ValidationMessages;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDTO(
    String name,

    @Email(message = ValidationMessages.EMAIL_INVALID)
    String email
) {}