package com.example.vestigioapi.application.vestigio.game.dto;

import com.example.vestigioapi.application.vestigio.game.move.AnswerType;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank(message = "Answer is required")
    AnswerType answer
) {}
