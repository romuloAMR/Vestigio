package com.example.vestigioapi.dto.game.move;

import com.example.vestigioapi.model.game.move.AnswerType;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank(message = "Answer is required")
    AnswerType answer
) {}
