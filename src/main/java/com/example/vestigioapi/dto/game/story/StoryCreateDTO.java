package com.example.vestigioapi.dto.game.story;

import com.example.vestigioapi.model.game.story.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoryCreateDTO(
    @NotBlank String title,
    @NotBlank String enigmaticSituation,
    @NotBlank String fullSolution,
    @NotNull Difficulty difficulty
) {}
