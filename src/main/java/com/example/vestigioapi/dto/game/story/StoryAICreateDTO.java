package com.example.vestigioapi.dto.game.story;

import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;
import com.example.vestigioapi.util.ValidationMessages;

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
