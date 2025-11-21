package com.example.vestigioapi.vestigio.dto;

import com.example.vestigioapi.vestigio.model.AnswerType;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank(message = "Answer is required")
    AnswerType answer
) {}
