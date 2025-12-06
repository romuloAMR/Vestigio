package com.example.vestigioapi.application.story.dto;

import com.example.vestigioapi.application.story.constants.Difficulty;
import com.example.vestigioapi.application.story.constants.Genre;
import com.example.vestigioapi.framework.engine.GameContent;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    String fullSolution,
    Genre genre,
    Difficulty difficulty,
    String creatorName
) implements GameContent {}
