package com.example.vestigioapi.dto.game.move;

import com.example.vestigioapi.model.game.move.AnswerType;
import jakarta.validation.constraints.NotNull;

public record AnswerQuestionRequestDTO(
    @NotNull Long moveId,
    @NotNull AnswerType answer
) {}