package com.example.vestigioapi.application.vestigio.game.dto;

import com.example.vestigioapi.application.vestigio.game.move.AnswerType;

import jakarta.validation.constraints.NotNull;

public record AnswerQuestionRequestDTO(
    @NotNull Long moveId,
    @NotNull AnswerType answer
) {}