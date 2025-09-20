package com.example.vestigioapi.dto.game.story;

import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.util.ValidationMessages;

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
    Difficulty difficulty
) {}
