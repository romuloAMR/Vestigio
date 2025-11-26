package com.example.vestigioapi.application.dto;

import com.example.vestigioapi.application.model.story.Difficulty;
import com.example.vestigioapi.application.model.story.Genre;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    String fullSolution,
    Genre genre,
    Difficulty difficulty,
    String creatorName
) {}
