package com.example.vestigioapi.application.vestigio.game.dto;

import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotNull;

public record GameSessionCreateDTO(
    @NotNull(message = ValidationMessages.ID_REQUIRED)
    Long storyId
) {}
