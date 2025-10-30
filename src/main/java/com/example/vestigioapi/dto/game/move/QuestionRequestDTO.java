package com.example.vestigioapi.dto.game.move;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequestDTO(
    @NotBlank(message = "Question text is required")
    String question
) {}
