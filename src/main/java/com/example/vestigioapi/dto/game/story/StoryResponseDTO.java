package com.example.vestigioapi.dto.game.story;

import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    Genre genre,
    Difficulty difficulty,
    String creatorName
) {}
