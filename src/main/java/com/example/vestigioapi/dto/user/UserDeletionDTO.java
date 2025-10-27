package com.example.vestigioapi.dto.user;

import com.example.vestigioapi.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;

public record UserDeletionDTO(
    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    String currentPassword
) {}