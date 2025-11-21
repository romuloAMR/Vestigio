package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;
import com.example.vestigioapi.vestigio.model.Difficulty;
import com.example.vestigioapi.vestigio.model.Genre;

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
