package com.example.vestigioapi.core.user.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;

public record UserDeletionDTO(
    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    String currentPassword
) {}