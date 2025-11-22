package com.example.vestigioapi.application.dto;

import com.example.vestigioapi.application.model.Difficulty;
import com.example.vestigioapi.application.model.Genre;
import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoryAICreateDTO(
    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    String title,
    
    @NotNull(message = ValidationMessages.GENRE_REQUIRED)
    Genre genre,

    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Difficulty difficulty
){}
