package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.vestigio.model.AnswerType;

import jakarta.validation.constraints.NotNull;

public record AnswerQuestionRequestDTO(
    @NotNull Long moveId,
    @NotNull AnswerType answer
) {}