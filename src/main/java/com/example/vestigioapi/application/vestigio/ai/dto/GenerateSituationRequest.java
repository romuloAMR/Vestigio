package com.example.vestigioapi.application.vestigio.ai.dto;

import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;
import com.example.vestigioapi.application.vestigio.story.constants.Genre;
import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateSituationRequest(
    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    String title,
    
    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Genre genre,

    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Difficulty difficulty
) {}
