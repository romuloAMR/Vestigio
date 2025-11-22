package com.example.vestigioapi.framework.user.dto;

import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDTO(
    String name,

    @Email(message = ValidationMessages.EMAIL_INVALID)
    String email
) {}