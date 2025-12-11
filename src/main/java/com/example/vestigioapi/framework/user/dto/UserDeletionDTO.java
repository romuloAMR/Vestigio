package com.example.vestigioapi.framework.user.dto;

import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;

public record UserDeletionDTO(
    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    String currentPassword
) {}