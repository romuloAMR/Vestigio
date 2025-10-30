package com.example.vestigioapi.dto.user;

import com.example.vestigioapi.util.ValidationMessages;
import com.example.vestigioapi.util.ValidationParam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordUpdateDTO(
    @NotBlank(message = ValidationMessages.OLD_PASSWORD_REQUIRED)
    String currentPassword,
    
    @NotBlank(message = ValidationMessages.NEW_PASSWORD_REQUIRED)
    @Size(min = ValidationParam.PASSWORD_MIN, message = ValidationMessages.NEW_PASSWORD_MIN)
    @Pattern(
        regexp = ValidationParam.PASSWORD_REGEX,
        message = ValidationMessages.NEW_PASSWORD_PATTERN
    )
    String newPassword,

    @NotBlank(message = ValidationMessages.CONFIRMATION_NEW_PASSWORD_REQUIRED)
    String confirmationPassword
) {}
