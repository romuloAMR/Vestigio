package com.example.vestigioapi.application.dto;

import com.example.vestigioapi.application.model.AnswerType;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank(message = "Answer is required")
    AnswerType answer
) {}
