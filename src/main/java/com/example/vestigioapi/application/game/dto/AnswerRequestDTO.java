package com.example.vestigioapi.application.game.dto;

import com.example.vestigioapi.application.game.move.AnswerType;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank(message = "Answer is required")
    AnswerType answer
) {}
