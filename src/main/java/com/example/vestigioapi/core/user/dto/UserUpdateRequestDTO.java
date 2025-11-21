package com.example.vestigioapi.core.user.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDTO(
    String name,

    @Email(message = ValidationMessages.EMAIL_INVALID)
    String email
) {}