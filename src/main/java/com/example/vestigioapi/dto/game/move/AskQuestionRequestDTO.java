package com.example.vestigioapi.dto.game.move;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequestDTO(
    @NotBlank String questionText
) {}