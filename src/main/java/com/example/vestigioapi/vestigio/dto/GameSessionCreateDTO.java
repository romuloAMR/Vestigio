package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;

import jakarta.validation.constraints.NotNull;

public record GameSessionCreateDTO(
    @NotNull(message = ValidationMessages.ID_REQUIRED)
    Long storyId
) {}
