package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.vestigio.model.Difficulty;
import com.example.vestigioapi.vestigio.model.Genre;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    String fullSolution,
    Genre genre,
    Difficulty difficulty,
    String creatorName
) {}
