package com.example.vestigioapi.dto.game.story;

import com.example.vestigioapi.model.game.story.Difficulty;

public record StoryResponseDTO(
    Long id,
    String title,
    String enigmaticSituation,
    Difficulty difficulty,
    String creatorName
) {}
