package com.example.vestigioapi.application.vestigio.game.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequestDTO(
    @NotBlank(message = "Question text is required")
    String question
) {}
