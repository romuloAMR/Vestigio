package com.example.vestigioapi.application.hangman.word.dto;

import com.example.vestigioapi.application.hangman.word.constants.ClassType;
import com.example.vestigioapi.application.hangman.word.constants.Difficulty;
import com.example.vestigioapi.framework.engine.GameContent;

public record WordResponseDTO(
    Long id,
    String name,
    ClassType type,
    Difficulty difficulty,
    String creatorName
) implements GameContent {}
