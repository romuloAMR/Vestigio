package com.example.vestigioapi.application.dto;

import com.example.vestigioapi.application.model.Difficulty;
import com.example.vestigioapi.application.model.Genre;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    String fullSolution,
    Genre genre,
    Difficulty difficulty,
    String creatorName
) {}
