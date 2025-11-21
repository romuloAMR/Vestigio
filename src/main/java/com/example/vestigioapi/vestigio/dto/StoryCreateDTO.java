package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.core.common.util.ValidationMessages;
import com.example.vestigioapi.vestigio.model.Difficulty;
import com.example.vestigioapi.vestigio.model.Genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoryCreateDTO(
    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    String title,
    
    @NotBlank(message = ValidationMessages.ENIGMATIC_SITUATION_REQUIRED)
    String enigmaticSituation,
    
    @NotBlank(message = ValidationMessages.FULL_SOLUTION_REQUIRED)
    String fullSolution,

    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Genre genre,
    
    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Difficulty difficulty
) {}
