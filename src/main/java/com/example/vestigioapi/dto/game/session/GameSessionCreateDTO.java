package com.example.vestigioapi.dto.game.session;

import com.example.vestigioapi.util.ValidationMessages;

import jakarta.validation.constraints.NotNull;

public record GameSessionCreateDTO(
    @NotNull(message = ValidationMessages.ID_REQUIRED)
    Long storyId
) {}
