package com.example.vestigioapi.application.dto;

import com.example.vestigioapi.application.model.AnswerType;

import jakarta.validation.constraints.NotNull;

public record AnswerQuestionRequestDTO(
    @NotNull Long moveId,
    @NotNull AnswerType answer
) {}