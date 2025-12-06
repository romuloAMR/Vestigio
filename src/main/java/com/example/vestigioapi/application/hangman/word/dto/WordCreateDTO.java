package com.example.vestigioapi.application.hangman.word.dto;

import com.example.vestigioapi.application.hangman.word.constants.ClassType;
import com.example.vestigioapi.application.hangman.word.constants.Difficulty;
import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WordCreateDTO(
    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    String name,

    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    ClassType type,
    
    @NotNull(message = ValidationMessages.DIFFICULTY_REQUIRED)
    Difficulty difficulty
) {}
